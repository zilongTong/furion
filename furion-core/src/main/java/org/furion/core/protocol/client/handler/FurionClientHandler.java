package org.furion.core.protocol.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.Timer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.furion.core.bean.PingRequest;
import org.furion.core.bean.eureka.Server;
import org.furion.core.context.ClientChannelLRUContext;
import org.furion.core.context.CountDownLatchLRUContext;
import org.furion.core.context.FurionResponse;
import org.furion.core.context.ResponseLRUContext;
import org.furion.core.protocol.client.FurionClientChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.furion.core.constants.Constants.REQUEST_ID;

/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-02
 */

public class FurionClientHandler extends ChannelInboundHandlerAdapter implements TimerTask {

    private static final Logger logger = LoggerFactory.getLogger(FurionClientHandler.class);
    //	FixedChannelPool fixedChannelPool=new FixedChannelPool(bootstrap, handler, maxConnections);
    private static final int TRY_TIMES = 3;
    private final Bootstrap bootstrap;
    private String host;
    private Server server;
    private final Timer timer;//定时器
    private int port;
    private static int reqTimeout;//请求超时时间
    private AtomicInteger integer = new AtomicInteger(0);
    private int attempts;//重试次数
    public ChannelHandler[] handlers;
    private volatile boolean reconnect = true;

    public FurionClientHandler(Bootstrap bootstrap, Timer timer, Server server,
                               boolean reconnect) {
        super();
        this.bootstrap = bootstrap;
        this.server = server;
        this.host = server.getHost();
        this.port = server.getPort();
        this.timer = timer;
        this.reconnect = reconnect;
        handlers = new ChannelHandler[]{
                this,
                new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
        };
        System.out.println("FurionClientHandler init...............");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        integer.incrementAndGet();
        System.out.println("userEventTriggered....................." + integer.get());
        System.out.println(evt.toString());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            System.out.println(event.state());
            // if(currentTime <= TRY_TIMES){
            System.out.println("心跳触发时间：" + new Date() + "heart beat currentTime:");
            // currentTime++;
            PingRequest pingRequest = new PingRequest(server);
            DefaultFullHttpRequest req = pingRequest.getHttpRequestInstance();

            ctx.channel().writeAndFlush(req);
            //  }

        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        channelRead1(ctx, msg);
//        ctx.fireChannelRead(msg);
        System.out.println("FurionClientHandler release msg");
        ReferenceCountUtil.release(msg);
    }

    /**
     * 每一个新的连接netty都会实例化一个HttpClientInboundHandler对象进行处理，
     * 只要同属一个HttpClientInboundHandler实例对象那HttpResponse 和 HttpContent肯定是对应的。
     *
     * @param ctx
     * @param httpObject
     * @throws Exception
     */
    private void channelRead1(ChannelHandlerContext ctx, Object httpObject)
            throws Exception {
        System.out.println("FurionClientHandler read..............");
        ClientChannelLRUContext.setFree((SocketChannel) ctx.channel());

        FurionResponse result = new FurionResponse();
        ByteBuf buf = Unpooled.EMPTY_BUFFER;
        String contentType = StringUtils.EMPTY;
        if (httpObject instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) httpObject;
            HttpHeaders headers = response.headers();
            headers.entries().stream().forEach(i -> {
                System.out.print(i.getKey() + ":");
                System.out.println(i.getValue());
            });
            contentType = response.headers().get(HttpHeaderNames.CONTENT_TYPE);
            String requestId = response.headers().get(REQUEST_ID);
            if (StringUtils.isEmpty(requestId)) {
                return;
            }
            result.setRequestId(NumberUtils.toLong(requestId));
        }
        if (httpObject instanceof HttpContent) {
            HttpContent content = (HttpContent) httpObject;
            buf = content.content();
            System.out.println(buf.toString(io.netty.util.CharsetUtil.UTF_8));
        }
        buf = copiedBuffer(buf.toString(io.netty.util.CharsetUtil.UTF_8), CharsetUtil.UTF_8);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(CONTENT_TYPE, contentType);
        result.setResponse(response);
        ResponseLRUContext.add(result.getRequestId(), result);
        CountDownLatchLRUContext.get(result.getRequestId()).countDown();
        // this.response = rpcResponse;
//        ResponseLRUMap.add(result.getRequestId(), result);
//        CountDownLatchLRUMap.get(result.getRequestId()).countDown();
        System.out.println("receive data" + result.toString());
        System.out.println(System.currentTimeMillis());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive.....................");
        System.out.println(System.currentTimeMillis());
        ClientChannelLRUContext.remove((SocketChannel) ctx.channel());
        System.out.println("链接关闭");
        if (reconnect) {
            System.out.println("链接关闭，将进行重连");
            if (attempts < TRY_TIMES) {
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

//        System.out.println("当前链路已经激活了，重连尝试次数重新置为0");
        attempts = 0;
        //判断连接结果，如果没有连接成功，则监听连接网络操作位SelectionKey.OP_CONNECT。如果连接成功，则调用pipeline().fireChannelActive()将监听位修改为READ。
        ctx.fireChannelActive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        System.out.println("exceptionCaught.....................");
        ClientChannelLRUContext.remove((SocketChannel) ctx.channel());
        ctx.close();
    }

    public int getReqTimeout() {
        return reqTimeout;
    }

    public void setReqTimeout(int reqTimeout) {
        this.reqTimeout = reqTimeout;
    }

    public void run(Timeout timeout) throws Exception {
        // TODO Auto-generated method stub

        System.out.println("timeTask run.....................");
        ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        try {
            synchronized (bootstrap) {
                bootstrap.handler(new FurionClientChannelInitializer(bootstrap, server, timer, reconnect));
                future = bootstrap.connect(host, port).sync();
            }
            //future对象
            future.addListener(new ChannelFutureListener() {

                public void operationComplete(ChannelFuture f) throws Exception {
                    boolean succeed = f.isSuccess();

                    //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                    if (!succeed) {
                        System.out.println("重连失败");
                        f.channel().pipeline().fireChannelInactive();
                    } else {
                        System.out.println("重连成功");
                        String keyString = host.concat(":").concat(String.valueOf(port));
                        ClientChannelLRUContext.add(keyString, (SocketChannel) f.channel());
                    }
                }
            });
        } catch (Exception e) {
        }

    }


}