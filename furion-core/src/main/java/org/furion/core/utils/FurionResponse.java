package org.furion.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;


/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-03
 */
public class FurionResponse extends DefaultFullHttpResponse {


    private String requestId;

    public FurionResponse(HttpVersion version, HttpResponseStatus status) {
        super(version, status);
    }

    public FurionResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, String requestId) {
        super(version, status, content);
        this.requestId = requestId;
    }

    public FurionResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, HttpHeaders headers, HttpHeaders trailingHeaders, String requestId) {
        super(version, status, content, headers, trailingHeaders);
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
