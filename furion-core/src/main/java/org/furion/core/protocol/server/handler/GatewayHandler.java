package org.furion.core.protocol.server.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class GatewayHandler extends ChannelInboundHandlerAdapter {



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest req) throws Exception {
        String uri = req.uri();
        Map<String, String> resMap = new HashMap<>();
        resMap.put("method", req.method().name());
        resMap.put("uri", uri);
        String msg = "<html><head><title>test</title></head><body>你请求uri为：" + uri + "</body></html>";
        // 创建http响应
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        // 设置头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        //response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        // 将html write到客户端
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
}