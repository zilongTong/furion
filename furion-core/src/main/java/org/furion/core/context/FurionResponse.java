package org.furion.core.context;

/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-03
 */
public class FurionResponse<T> {


    private T response;

    private Long requestId;

    public FurionResponse() {
    }

    public FurionResponse(T response, Long requestId) {
        this.response = response;
        this.requestId = requestId;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}
