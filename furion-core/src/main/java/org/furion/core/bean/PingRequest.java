package org.furion.core.bean;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;

import io.netty.handler.codec.http.HttpVersion;
import org.furion.core.bean.eureka.Server;
import org.furion.core.enumeration.ContentType;
import org.furion.core.utils.id.GeneratorEnum;
import org.furion.core.utils.id.KeyGeneratorFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;


public class PingRequest {

    private String host;
    private int port;
    private static DefaultFullHttpRequest request;

    private Long requestId;
    private Server server;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public PingRequest(Server server) {
        this.server = server;
        this.host = server.getHost();
        this.port = server.getPort();
        this.requestId = KeyGeneratorFactory.gen(GeneratorEnum.IP).generate();
        URI uri = null;
        try {
            uri = new URI(FurionConstants.HTTP_PRE.concat(this.host).concat(FurionConstants.SEPARATE_COLON).concat(String.valueOf(this.port)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        ByteBuf content = null;
//        try {
////            content = Unpooled.wrappedBuffer("ping".getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.HEAD, uri.toASCIIString());
//        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        request.headers().set(HttpHeaderNames.HOST, this.host);
//        request.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    }

    public DefaultFullHttpRequest getHttpRequestInstance() {
        return request;
    }


}
