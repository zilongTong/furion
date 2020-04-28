package org.furion.core.exception;


public class UnknownFurionFilterException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnknownFurionFilterException() {
        super(String.format("Unknown FilterException!"));
    }
}
