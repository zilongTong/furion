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
public class FurionResponse {


    private DefaultFullHttpResponse response;

    private Long requestId;

    public FurionResponse() {
    }

    public FurionResponse(DefaultFullHttpResponse response, Long requestId) {
        this.response = response;
        this.requestId = requestId;
    }

    public DefaultFullHttpResponse getResponse() {
        return response;
    }

    public void setResponse(DefaultFullHttpResponse response) {
        this.response = response;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
