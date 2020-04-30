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
        FurionFilterRegistry registry = FurionGatewayContext.getRegistry();
        FurionFilterRegistry.Node<FurionFilter> node = (FurionFilterRegistry.Node<FurionFilter>) registry.getHeadFilter();
        doFilter(node);
    }

    private void doFilter(FurionFilterRegistry.Node<FurionFilter> node) {
//        if (node.getItem().shouldFilter0()) {
//            node.getItem().init(requestId, channel).run();
//        }
//        if (node.hasNext()) {
//            doFilter(node.getSuccessor(node));
//        }
    }

}
