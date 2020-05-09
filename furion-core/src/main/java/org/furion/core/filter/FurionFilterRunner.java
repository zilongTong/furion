package org.furion.core.filter;


import io.netty.channel.Channel;
import org.furion.core.context.FurionGatewayContext;

public class FurionFilterRunner {


    private final Long requestId;

    private final Channel channel;

    public FurionFilterRunner(Long requestId, Channel channel) {
        this.requestId = requestId;
        this.channel = channel;
    }

    public void filter() {
        FurionFilterRegistry registry = FurionGatewayContext.getInstance().getRegistry();
        FurionFilter filter = registry.getHeadFilter();
        doFilter(filter,registry);
    }

    private void doFilter(FurionFilter filter, FurionFilterRegistry registry) {
        if (filter.shouldFilter0()) {
            filter.init(requestId, channel).run();
        }
        if (registry.hasNext(filter)) {
            doFilter(registry.getSuccessor(filter),registry);
        }
    }

}
