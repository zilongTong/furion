package org.furion.core.ribbon;

import org.furion.core.bean.eureka.Server;

public interface IRule {

    public Server choose(Object key);

    public void setLoadBalancer(ILoadBalancer lb);

    public ILoadBalancer getLoadBalancer();
}
