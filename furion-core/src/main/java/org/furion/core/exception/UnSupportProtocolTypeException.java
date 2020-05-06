package org.furion.core.exception;

import org.furion.core.enumeration.ProtocolType;

public class UnSupportProtocolTypeException extends RuntimeException {

    public UnSupportProtocolTypeException(ProtocolType protocolType) {
        super(String.format("Unknown SupportProtocolException: %1$s", protocolType));
    }
}
