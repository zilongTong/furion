package org.furion.core.circuitbreaker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreakerContext {

    private static ConcurrentHashMap<Resource, AtomicInteger> throwExceptionThreshold = new ConcurrentHashMap<>();


}
