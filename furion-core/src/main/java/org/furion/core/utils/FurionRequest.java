package org.furion.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.io.Serializable;

/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-03
 */
public class FurionRequest implements Serializable {
    private static final long serialVersionUID = 1293417361623232069L;

    private Long requestId;

    private FullHttpRequest request;


    public FurionRequest() {
    }

    public FurionRequest(Long requestId, FullHttpRequest request) {
        this.requestId = requestId;
        this.request = request;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
