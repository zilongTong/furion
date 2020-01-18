package org.furion.core.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.io.Serializable;

/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-03
 */
public class FurionRequest extends DefaultFullHttpRequest implements Serializable {
    private static final long serialVersionUID = 1293417361623232069L;

    private String requestId;

    public FurionRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
        this.requestId = requestId;

    }

    public FurionRequest(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
        super(httpVersion, method, uri, content);
        this.requestId = requestId;

    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
