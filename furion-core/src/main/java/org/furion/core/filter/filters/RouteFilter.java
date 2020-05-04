package org.furion.core.filter.filters;


import org.furion.core.bean.eureka.Server;
import org.furion.core.context.FurionResponse;
import org.furion.core.context.RequestCommand;
import org.furion.core.context.RequestLRUContext;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.FurionException;
import org.furion.core.filter.FurionFilter;
import org.furion.core.protocol.client.http.HttpNetFactory;
import org.furion.core.protocol.client.http.HttpNetWork;


public class RouteFilter extends FurionFilter {


    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws FurionException {
        getCurrentExclusiveOwnerChannel();
        Long requestId = getCurrentExclusiveOwnerRequestId();
        RequestCommand command = new RequestCommand();
        command.setRequestId(requestId);
        command.setRequest(RequestLRUContext.get(requestId).getRequest());
        HttpNetWork httpNetWork = HttpNetFactory.fetchProcessor(ProtocolType.NETTY, new Server("127.0.0.1", 8080));
        FurionResponse response = (FurionResponse) httpNetWork.send(command);
//        ChannelFuture future = channel.writeAndFlush();
        writeAndFlush(response.getResponse());
        return null;
    }

}
