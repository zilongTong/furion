package org.furion.core.context;

import org.furion.core.exception.FurionException.Status;

public class FurionFilterResult {


    private Object result;
    private Throwable exception;
    private Status status;

    public FurionFilterResult(Object result, Status status) {
        this.result = result;
        this.status = status;
    }

    public FurionFilterResult(Status status) {
        this.status = status;
    }

    public FurionFilterResult() {
        this.status = Status.DISABLED;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
