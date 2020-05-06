package org.furion.core.context;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import okhttp3.Request;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.UnSupportProtocolTypeException;
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

    private RequestWrapper nettyRequest;

    private RequestWrapper okHttpRequest;


    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public RequestWrapper getNettyRequest() {
        return nettyRequest;
    }

    public void setNettyRequest(RequestWrapper nettyRequest) {
        this.nettyRequest = nettyRequest;
    }

    public RequestWrapper getOkHttpRequest() {
        return okHttpRequest;
    }

    public void setOkHttpRequest(RequestWrapper okHttpRequest) {
        this.okHttpRequest = okHttpRequest;
    }

    public RequestCommand(Long requestId, FullHttpRequest request) {
        this.requestId = requestId;
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
    }

    public RequestCommand builder() {
        nettyRequestBuilder();
        okHttpRequestBuilder();
        return this;
    }

    public void nettyRequestBuilder() {
        try {
//            FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(httpVersion, method,
//                    uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
//            fullHttpRequest.headers().set(HttpHeaderNames.HOST, host);
//            fullHttpRequest.headers().set(REQUEST_ID, requestId);
//            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            nettyRequest = new RequestWrapper().nettyWrapper(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void okHttpRequestBuilder() {
        try {
//            FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(httpVersion, method,
//                    uri.toASCIIString(), Unpooled.wrappedBuffer(content.getBytes("UTF-8")));
//            fullHttpRequest.headers().set(HttpHeaderNames.HOST, host);
//            fullHttpRequest.headers().set(REQUEST_ID, requestId);
//            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
//            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            okHttpRequest = new RequestWrapper().okHttpWrapper(null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public RequestWrapper getRequest(ProtocolType type) {
        if (type == ProtocolType.NETTY) {
            return nettyRequest;
        }
        if (type == ProtocolType.OK_HTTP) {
            return okHttpRequest;
        }
        throw new UnSupportProtocolTypeException(type);

    }

}
