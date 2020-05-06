package org.furion.core.ribbon;

import org.furion.core.bean.eureka.Server;
import org.furion.core.discovery.EurekaNetWork;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRibbonRule extends AbstractLoadBalancerRule {

    AtomicInteger atomicInteger = new AtomicInteger();

    @Override
    public Server choose(String key) {
        List<Server> serverList = EurekaNetWork.getServerList(key);
        if (!serverList.isEmpty())
            return serverList.get(atomicInteger.getAndIncrement() % serverList.size());
        return null;
    }
}