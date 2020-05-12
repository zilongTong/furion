package org.furion.core.ratelimiter;

public interface LifeCycle {

    void start();

    void stop();

    boolean isStarted();
}
