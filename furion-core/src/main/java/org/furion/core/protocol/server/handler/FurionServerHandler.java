package org.furion.core.protocol.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.StringUtil;
import org.furion.core.constants.Constants;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.RequestCommand;
import org.furion.core.context.RequestLRUContext;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.enumeration.PropertiesSource;
import org.furion.core.filter.FurionFilterRunner;
import org.furion.core.filter.filters.RouteFilter;
import org.furion.core.protocol.server.FurionServerNetWork;
import org.furion.core.utils.JsonUtil;
import org.furion.core.utils.id.GeneratorEnum;
import org.furion.core.utils.id.KeyGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.furion.core.enumeration.ConnectionState;

import java.io.FileOutputStream;
import java.util.Properties;

import static org.furion.core.constants.Constants.REQUEST_ID;

import static org.furion.core.enumeration.ConnectionState.DISCONNECTED;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class FurionServerHandler extends ChannelInboundHandlerAdapter {


    protected final static Logger LOG = LoggerFactory.getLogger(FurionServerHandler.class);


    protected volatile ChannelHandlerContext ctx;
    protected volatile Channel channel;
    PropertiesManager propertiesManager;

    private volatile ConnectionState currentState;
    private volatile boolean tunneling = false;
    protected volatile long lastReadTime = 0;

    private FurionServerNetWork netWork;

    private FurionFilterRunner runner;

    private boolean readingChunks;

    private FullHttpRequest fullHttpRequest;

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

    private HttpPostRequestDecoder decoder;

    public FurionServerHandler(FurionServerNetWork netWork) {
        this.netWork = netWork;
        propertiesManager = FurionGatewayContext.getInstance().getPropertiesManager();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("server caught exception", cause);
        ctx.close();
    }


    protected void connected() {
        System.out.println("connected-------------" + netWork.getAllChannels().toString());
        LOG.debug("Connected");
    }

    protected void become(ConnectionState state) {
        this.currentState = state;
    }

    /**
     * This method is called as soon as the underlying {@link Channel} becomes
     * disconnected.
     */
    protected void disconnected() {
        become(DISCONNECTED);
        System.out.println("server socket disconnected-------------" + netWork.getAllChannels().toString());
        LOG.debug("Disconnected");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
            this.ctx = ctx;
            this.channel = ctx.channel();
            this.netWork.registerChannel(ctx.channel());
        } finally {
            super.channelRegistered(ctx);
        }
    }

    @Override
    public final void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            connected();
            System.out.println(netWork.getAllChannels().toString());
        } finally {
            super.channelActive(ctx);
        }
    }

    /**
     * As soon as the Netty Channel is inactive, we recognize the
     * ProxyConnection as disconnected.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            disconnected();
        } finally {
            super.channelInactive(ctx);
        }
    }

    protected ChannelFuture writeToChannel(final Object msg) {
        return channel.writeAndFlush(msg);
    }

    Future<Void> disconnect() {
        if (channel == null) {
            return null;
        } else {

            final Promise<Void> promise = channel.newPromise();
            writeToChannel(Unpooled.EMPTY_BUFFER).addListener(
                    new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(
                                Future<? super Void> future)
                                throws Exception {
                            closeChannel(promise);
                        }
                    });
            return promise;
        }
    }

    private void closeChannel(final Promise<Void> promise) {
        channel.close().addListener(
                new GenericFutureListener<Future<? super Void>>() {
                    public void operationComplete(
                            Future<? super Void> future)
                            throws Exception {
                        if (future
                                .isSuccess()) {
                            promise.setSuccess(null);
                        } else {
                            promise.setFailure(future
                                    .cause());
                        }
                    }
                });
    }

    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        System.out.println("userEventTriggered---------------------");
        try {
            if (evt instanceof IdleStateEvent) {
                LOG.debug("Got idle");
                disconnect();
            }
        } finally {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(ctx.channel());
        System.out.println("server channel read...........");
        if (msg instanceof FullHttpRequest) {
            System.out.println("FullHttpRequest...........");
            Long uid = KeyGeneratorFactory.gen(GeneratorEnum.IP).generate();
            this.fullHttpRequest = (FullHttpRequest) msg;
            fullHttpRequest.headers().set(REQUEST_ID, uid);
            RequestCommand command = new RequestCommand(uid, fullHttpRequest);
            RequestLRUContext.add(uid, command);
            runner = new FurionFilterRunner(uid, channel);
            //url大致分为配置类、监控类和普通请求
            String uri = fullHttpRequest.getUri();
            if(uri.endsWith("ico")){//过滤favicon.ico请求
                return;
            }else if (uri.startsWith(Constants.CONFIG_PATH)) {//处理配置请求
                channel.writeAndFlush(updateConfig(uri,fullHttpRequest));
            }else if (uri.startsWith(Constants.MONITOR_PATH)){//处理监控请求

            }else {
                runner.filter();
            }
            try {
                ReferenceCountUtil.release(msg);
            } catch (IllegalReferenceCountException e) {
                System.out.println("server handler ReferenceCountUtil error");
            }
        }
    }

    public FullHttpResponse updateConfig(String uri, FullHttpRequest fullHttpRequest) {
        try {
            ByteBuf byteBuf = fullHttpRequest.content();
            byte[] src = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(src);
            switch (uri) {
                case Constants.CONFIG_PATH_SYSTEM:
                    break;
                case Constants.CONFIG_PATH_FURION:
                    Properties properties = JsonUtil.getObject(src, Properties.class);
                    propertiesManager.refresh(PropertiesSource.NET,properties);
                    break;
                case Constants.CONFIG_PATH_FIELTER:
                    String filterPath = RouteFilter.class.getResource("/filter").getPath()+"/";
                    String fileName = getJavaFileName(new String(src));
                    if(!StringUtil.isNullOrEmpty(fileName)){
                        try(FileOutputStream fos = new FileOutputStream(filterPath+fileName)){
                            fos.write(src);
                            fos.flush();
                        }
                    }
                    break;
                default:
                    break;

            }
            return getResponse(HttpResponseStatus.OK,"更新配置成功");
        }catch (Exception e){
            System.out.println("更新配置失败"+e);
            return getResponse(HttpResponseStatus.BAD_REQUEST,"更新配置失败");
        }

    }

    private String getJavaFileName(String src){
        int startIndex = src.indexOf("class")+5;
        int endIndex = src.indexOf("extends");
        if(startIndex > 0 && endIndex > 0 && startIndex<=endIndex){
            return src.substring(startIndex,endIndex).replaceAll(" ","").concat(".java");
        }else {
            System.out.println("java源文件格式异常");
            return "";
        }
    }

    private FullHttpResponse getResponse(HttpResponseStatus httpResponseStatus,String msg){
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        fullHttpResponse.content().writeBytes(msg.getBytes());
        return fullHttpResponse;
    }

}