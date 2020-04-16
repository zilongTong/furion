package org.furion.core.ribbon;

import org.furion.core.bean.eureka.Server;

import java.util.List;

public abstract class AbstractLoadBalancer implements ILoadBalancer{


    public Server chooseServer() {
        return this.chooseServer((Object)null);
    }

    public abstract List<Server> getServerList(AbstractLoadBalancer.ServerGroup var1);



    public static enum ServerGroup {
        ALL,
        STATUS_UP,
        STATUS_NOT_UP;
        private ServerGroup() {
        }
    }

}
