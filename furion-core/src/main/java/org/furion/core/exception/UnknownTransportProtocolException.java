package org.furion.core.exception;

import org.furion.core.enumeration.ProtocolType;


public class UnknownTransportProtocolException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnknownTransportProtocolException(ProtocolType protocolType) {
        super(String.format("Unknown TransportProtocol: %1$s", protocolType));
    }
}
