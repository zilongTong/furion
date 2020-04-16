package org.furion.core.ribbon;

public abstract class AbstractLoadBalancerRule implements IRule {

    private ILoadBalancer lb;

    public void setLoadBalancer(ILoadBalancer lb) {
        this.lb = lb;
    }

    public ILoadBalancer getLoadBalancer() {
        return this.lb;
    }
}
