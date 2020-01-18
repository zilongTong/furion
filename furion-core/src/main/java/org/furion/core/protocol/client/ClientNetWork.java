package org.furion.core.protocol.client;

import org.furion.core.protocol.client.handler.FurionClientHandler;
import org.furion.core.protocol.client.lru.CountDownLatchLRUMap;
import org.furion.core.protocol.client.lru.ResponseLRUMap;
import org.furion.core.utils.FurionResponse;
import org.furion.core.protocol.client.lru.ClientChannelLRUMap;
import org.furion.core.utils.FurionRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
@Slf4j
public class ClientNetWork {

    private static Long requestTimeout;
    private static int port;
    private static String host;
    private final HashedWheelTimer timer = new HashedWheelTimer();

    private Bootstrap bootstrap = new Bootstrap();


    public ClientNetWork(String host, int port, Long requestTimeout) {
        this.port = port;
        this.host = host;
        this.requestTimeout = requestTimeout;
    }

    private void connect(final int port, final String host) {

        EventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                /**
                                 * 或者使用HttpRequestDecoder & HttpResponseEncoder
                                 */
                                .addLast(new HttpServerCodec())
                                /**
                                 * 在处理POST消息体时需要加上
                                 */
                                .addLast(new HttpObjectAggregator(1024 * 1024))
                                .addLast(new HttpServerExpectContinueHandler())
                                .addLast(new FurionClientHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
//            URI uri = new URI("http;");
//            FurionRequest request = null;
//            request = new FurionRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(), Unpooled.wrappedBuffer(("").getBytes("UTF-8")));
//            request.headers().set("HOST", host);
//            request.headers().add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
//            SocketChannel channel = (SocketChannel) future.channel();
//            channel.writeAndFlush(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public FurionResponse send(FurionRequest request) {
        String key = host.concat(":").concat(String.valueOf(port));
        while (true) {
            Channel channel = ClientChannelLRUMap.get(key);
            if (channel != null) {
                channel.writeAndFlush(request);
                break;
            }
            connect(port, host);
        }
        CountDownLatch waitLatch = new CountDownLatch(1);
        CountDownLatchLRUMap.add(request.getRequestId(), waitLatch);
        try {
            waitLatch.await(requestTimeout, TimeUnit.MILLISECONDS);
            FurionResponse response = ResponseLRUMap.get(request.getRequestId());
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
