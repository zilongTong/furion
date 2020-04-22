package org.furion.core.context;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.furion.core.utils.id.GeneratorEnum;
import org.furion.core.utils.id.KeyGeneratorFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import static org.furion.core.constants.Constants.REQUEST_ID;

public class RequestCommand {


    private HttpVersion httpVersion;
    private HttpMethod method;
    private URI uri;
    private String host;
    private String content;
    private Long requestId;

    private FullHttpRequest request;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public RequestCommand() {
    }

    public RequestCommand(HttpVersion httpVersion, HttpMethod method, URI uri, String host, String content, Long requestId) {
        this.httpVersion = httpVersion;
        this.method = method;
        this.uri = uri;
        this.host = host;
        this.content = content;
        this.requestId = requestId;
        this.requestId = KeyGeneratorFactory.gen(GeneratorEnum.IP).generate();
        builder();
    }

    public void builder() {
        try {
            request = new DefaultFullHttpRequest(httpVersion, method,
                    uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(REQUEST_ID, requestId);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
