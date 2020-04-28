package org.furion.core.protocol.server.handler;

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
import org.furion.core.bean.eureka.Server;
import org.furion.core.context.FurionRequest;
import org.furion.core.context.FurionResponse;
import org.furion.core.context.RequestCommand;
import org.furion.core.context.RequestLRUContext;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.filter.FurionFilterRunner;
import org.furion.core.protocol.client.http.HttpNetFactory;
import org.furion.core.protocol.client.http.HttpNetWork;
import org.furion.core.protocol.server.FurionServerNetWork;
import org.furion.core.utils.id.GeneratorEnum;
import org.furion.core.utils.id.KeyGeneratorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.furion.core.enumeration.ConnectionState;

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
        System.out.println("Disconnected-------------" + netWork.getAllChannels().toString());
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
        System.out.println("read...........");
        if (msg instanceof FullHttpRequest) {
            System.out.println("FullHttpRequest...........");
            Long uid = KeyGeneratorFactory.gen(GeneratorEnum.IP).generate();
            this.fullHttpRequest = (FullHttpRequest) msg;
            FurionRequest request = new FurionRequest();
            request.setRequestId(uid);
            request.setRequest(fullHttpRequest);
            fullHttpRequest.headers().set(REQUEST_ID, uid);
            RequestLRUContext.add(uid, request);
            runner = new FurionFilterRunner(uid, channel);
            runner.filter();
            try {
                ReferenceCountUtil.release(msg);
            } catch (IllegalReferenceCountException e) {

            }
        }
    }

//    @Override
//    public void channelRead1(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("=============================================");
//        System.out.println(msg.toString());
//        if (msg instanceof FullHttpRequest) {
//
//
//            this.fullHttpRequest = (FullHttpRequest) msg;
//            fullHttpRequest.headers().set(REQUEST_ID, KeyGeneratorFactory.gen(GeneratorEnum.IP).generate());
//
////        if (HttpDemoServer.isSSL) {
////            System.out.println("Your session is protected by " +
////                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
////                    " cipher suite.\n");
////        }
//            /**
//             * 在服务器端打印请求信息
//             */
//            System.out.println("VERSION: " + fullHttpRequest.getProtocolVersion().text() + "\r\n");
//            System.out.println("REQUEST_URI: " + fullHttpRequest.getUri() + "\r\n\r\n");
//            System.out.println("\r\n\r\n");
//            for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
//                System.out.println("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
//            }
//
//            /**
//             * 服务器端返回信息
//             */
//            responseContent.setLength(0);
//            responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
//            responseContent.append("===================================\r\n");
//
//            responseContent.append("VERSION: " + fullHttpRequest.getProtocolVersion().text() + "\r\n");
//            responseContent.append("REQUEST_URI: " + fullHttpRequest.getUri() + "\r\n\r\n");
//            responseContent.append("\r\n\r\n");
//            for (Map.Entry<String, String> entry : fullHttpRequest.headers()) {
//                responseContent.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
//            }
//            responseContent.append("\r\n\r\n");
//            Set<Cookie> cookies;
//            String value = fullHttpRequest.headers().get(COOKIE);
//            if (value == null) {
//                cookies = Collections.emptySet();
//            } else {
//                cookies = CookieDecoder.decode(value);
//            }
//            for (Cookie cookie : cookies) {
//                responseContent.append("COOKIE: " + cookie.toString() + "\r\n");
//            }
//            responseContent.append("\r\n\r\n");
//
//
//            QueryStringDecoder queryDecoder = new QueryStringDecoder(fullHttpRequest.uri());
//            final String uri = fullHttpRequest.getUri();
//            HttpMethod method = fullHttpRequest.getMethod();
//            // ????
//            Map<String, String> params = null;
//            // GET
//            if (method == HttpMethod.GET) {
//                Map<String, List<String>> getParams = queryDecoder.parameters();
//                System.out.println(getParams);
//            }
//            // POST
//            else if (method == HttpMethod.POST) {
//                ByteBuf content = fullHttpRequest.content();
//                if (content.isReadable()) {
//                    String param = content.toString(Charset.forName("UTF-8"));
//                    QueryStringDecoder postQueryStringDecoder = new QueryStringDecoder("/?" + param);
//                    Map<String, List<String>> postParams = postQueryStringDecoder.parameters();
//                    System.out.println(postParams);
//                }
//            }
//            final String path = queryDecoder.path();
//            System.out.println(path);
//
//            if (fullHttpRequest.getMethod().equals(HttpMethod.GET)) {
//                //get请求
//                QueryStringDecoder decoderQuery = new QueryStringDecoder(fullHttpRequest.uri());
//                String query = decoderQuery.rawQuery();
//                System.out.println(query);
//                Map<String, List<String>> uriAttributes = decoderQuery.parameters();
//                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
//                    for (String attrVal : attr.getValue()) {
//                        System.out.println("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
//                        responseContent.append("URI: " + attr.getKey() + '=' + attrVal + "\r\n");
//                    }
//                }
//                responseContent.append("\r\n\r\n");
//
//                responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
//                writeResponse(ctx.channel());
//                return;
//            } else if (fullHttpRequest.method().equals(HttpMethod.POST)) {
//                //post请求
//
//
//                decoder = new HttpPostRequestDecoder(factory, fullHttpRequest);
//                readingChunks = HttpHeaders.isTransferEncodingChunked(fullHttpRequest);
//                responseContent.append("Is Chunked: " + readingChunks + "\r\n");
//                responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");
//
//                try {
//                    while (decoder.hasNext()) {
//                        InterfaceHttpData data = decoder.next();
//                        if (data != null) {
//                            try {
//                                writeHttpData(data);
//                            } finally {
//                                data.release();
//                            }
//                        }
//                    }
//                } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
//                    responseContent.append("\r\n\r\nEND OF POST CONTENT\r\n\r\n");
//                }
//                writeResponse(ctx.channel());
//                return;
//            } else {
//                System.out.println("discard.......");
//                return;
//            }
//        }
//        ReferenceCountUtil.release(msg);
//    }
//
//    private void reset() {
////        fullHttpRequest = null;
//        // destroy the decoder to release all resources
//        decoder.destroy();
//        decoder = null;
//    }
//
//    private void writeHttpData(InterfaceHttpData data) {
//
//        /**
//         * HttpDataType有三种类型
//         * Attribute, FileUpload, InternalAttribute
//         */
//        if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
//            Attribute attribute = (Attribute) data;
//            String value;
//            try {
//                value = attribute.getValue();
//                System.out.println(value);
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
//                        + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n");
//                return;
//            }
//            if (value.length() > 100) {
//                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
//                        + attribute.getName() + " data too long\r\n");
//            } else {
//                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
//                        + attribute.toString() + "\r\n");
//            }
//        }
//    }
//
//
//    /**
//     * http返回响应数据
//     *
//     * @param channel
//     */
//    private void writeResponse(Channel channel, StringBuilder responseContent) {
//        System.out.println(responseContent.toString());
//        // Convert the response content to a ChannelBuffer.
//        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
//        responseContent.setLength(0);
//
//        // Decide whether to close the connection or not.
//        boolean close = fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
//                || fullHttpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
//                && !fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
//
//        // Build the response object.
//        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
//        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
//
//        if (!close) {
//            // There's no need to add 'Content-Length' header
//            // if this is the last response.
//            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
//        }
//
//        Set<Cookie> cookies;
//        String value = fullHttpRequest.headers().get(COOKIE);
//        if (value == null) {
//            cookies = Collections.emptySet();
//        } else {
//            cookies = CookieDecoder.decode(value);
//        }
//        if (!cookies.isEmpty()) {
//            // Reset the cookies if necessary.
//            for (Cookie cookie : cookies) {
//                response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
//            }
//        }
//        // Write the response.
//        ChannelFuture future = channel.writeAndFlush(response);
//        // Close the connection after the write operation is done if necessary.
//        if (close) {
//            future.addListener(ChannelFutureListener.CLOSE);
//        }
//    }


}