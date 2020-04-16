package org.furion.core.ribbon;

import org.furion.core.bean.eureka.Server;

public class RoundRobinRule extends AbstractLoadBalancerRule {

    @Override
    public Server choose(Object key) {
        return null;
    }
}
