package org.furion.core.exception;

public class NoAvailableSocketChannelException extends RuntimeException {

    public NoAvailableSocketChannelException(Long traceId) {
        super(String.format("No Available SocketChannel Exception  traceId: %1$s ", traceId));
    }

}
