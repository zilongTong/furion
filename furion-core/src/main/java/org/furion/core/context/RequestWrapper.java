package org.furion.core.context;

import io.netty.handler.codec.http.FullHttpRequest;
import okhttp3.Request;

import java.io.Serializable;

public class RequestWrapper implements Serializable {

    private FullHttpRequest nettyRequest;

    private Request okHttpRequest;


    public RequestWrapper() {
    }

    public RequestWrapper nettyWrapper(FullHttpRequest nettyRequest) {
        this.nettyRequest = nettyRequest;
        return this;
    }

    public RequestWrapper okHttpWrapper(Request okHttpRequest) {
        this.okHttpRequest = okHttpRequest;
        return this;
    }

    public FullHttpRequest getNettyRequest() {
        return nettyRequest;
    }

    public void setNettyRequest(FullHttpRequest nettyRequest) {
        this.nettyRequest = nettyRequest;
    }

    public Request getOkHttpRequest() {
        return okHttpRequest;
    }

    public void setOkHttpRequest(Request okHttpRequest) {
        this.okHttpRequest = okHttpRequest;
    }
}
