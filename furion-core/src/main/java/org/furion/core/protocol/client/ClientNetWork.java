package org.furion.core.protocol.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.furion.core.bean.eureka.Server;
import org.furion.core.context.ClientChannelLRUContext;
import org.furion.core.context.CountDownLatchLRUContext;
import org.furion.core.context.RequestCommand;
import org.furion.core.context.RequestLRUContext;
import org.furion.core.context.ResponseLRUContext;

import org.furion.core.enumeration.ProtocolType;
import org.furion.core.protocol.client.http.HttpNetFactory;
import org.furion.core.protocol.client.http.HttpNetWork;
import org.furion.core.utils.FurionResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import org.furion.core.utils.id.GeneratorEnum;
import org.furion.core.utils.id.KeyGeneratorFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
@Slf4j
public class ClientNetWork implements HttpNetWork<RequestCommand, FurionResponse> {

    private static Long requestTimeout = 10000L;
    private static int port;
    private static String host;
    private final HashedWheelTimer timer;
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Server server;


    private Bootstrap bootstrap = new Bootstrap();


    public ClientNetWork(HashedWheelTimer t, Server s) {
        server = s;
        timer = t;
        this.host = server.getHost();
        this.port = server.getPort();
    }


    private void connect() {
//        FurionClientHandler handler = new FurionClientHandler(bootstrap, timer, server, true);
        EventLoopGroup group = new NioEventLoopGroup();
        String keyString = host.concat(":").concat(String.valueOf(port));
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new FurionClientChannelInitializer(bootstrap, server, timer, true))
                .option(ChannelOption.SO_KEEPALIVE, true);
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
//            RpcRequest request=new RpcRequest();
//            request.setBaseMsg(new PingMsg());
//            future.channel().writeAndFlush(request);
            ClientChannelLRUContext.add(keyString, (SocketChannel) future.channel());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FurionResponse send(RequestCommand requestCommand) {

        String key = host.concat(":").concat(String.valueOf(port));
        while (true) {
            Channel channel = ClientChannelLRUContext.get(key);
            if (channel != null) {
                channel.writeAndFlush(requestCommand.getRequest());
                break;
            }
            connect();
        }
        CountDownLatch waitLatch = new CountDownLatch(1);
        CountDownLatchLRUContext.add(requestCommand.getRequestId(), waitLatch);
        try {
            waitLatch.await(requestTimeout, TimeUnit.MILLISECONDS);
            FurionResponse response = ResponseLRUContext.get(requestCommand.getRequestId());

            System.out.println(response);
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            release(requestCommand.getRequestId());
        }
        return null;
    }

    void release(Long id) {
        CountDownLatchLRUContext.remove(id);
        ResponseLRUContext.remove(id);
        RequestLRUContext.remove(id);
    }

    public static void main(String[] args) {
        new Thread("111") {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++)
                    runTest();
            }
        }.start();
        new Thread("222") {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++)
                    runTest();
            }
        }.start();

    }

    private static void runTest() {
        HttpNetWork httpNetWork = HttpNetFactory.fetchProcessor(ProtocolType.NETTY, new Server("127.0.0.1", 8080));
        try {

            LoginDTO dto = new LoginDTO();
            dto.setPassword("111");
            dto.setUsername("222");

            URI uri = new URI("/login");
            RequestCommand request = null;
            try {
                request = new RequestCommand(HttpVersion.HTTP_1_1, HttpMethod.POST, uri, "127.0.0.1", mapper.writeValueAsString(dto), KeyGeneratorFactory.gen(GeneratorEnum.IP).generate());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


//            setCommand(request);


            httpNetWork.send(request.getRequest());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}
