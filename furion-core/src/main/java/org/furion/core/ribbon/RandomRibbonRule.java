package org.furion.core.ribbon;

import org.furion.core.annotation.SpiMetadata;
import org.furion.core.bean.eureka.Server;
import org.furion.core.discovery.EurekaNetWork;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpiMetadata(name = "RandomRibbon")
public class RandomRibbonRule extends AbstractLoadBalancerRule {

    @Override
    public Server choose(String key) {
        List<Server> serverList = EurekaNetWork.getServerList(key);
        if (!serverList.isEmpty())
            return serverList.get(ThreadLocalRandom.current().nextInt(serverList.size()));
        return null;
    }
}

