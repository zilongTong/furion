package org.furion.core.exception;

import org.furion.core.enumeration.ProtocolType;

public class FilterExclusiveThreadLocalReleasedException extends RuntimeException {


    public FilterExclusiveThreadLocalReleasedException(String var) {
        super(String.format("exclusiveThreadLocal had: %1$s", var));
    }
}
