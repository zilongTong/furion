package org.furion.core.filter.filters;


import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;
import org.furion.core.bean.eureka.Server;
import org.furion.core.context.*;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.FurionException;
import org.furion.core.filter.FurionFilter;
import org.furion.core.protocol.client.http.HttpNetFactory;
import org.furion.core.protocol.client.http.HttpNetWork;
import org.furion.core.ribbon.AbstractLoadBalancerRule;
import org.furion.core.utils.FurionServiceLoader;
import org.furion.core.utils.UrlMatchUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.furion.core.constants.Constants.REQUEST_ID;


public class RouteFilter extends FurionFilter {

    FurionServiceLoader<AbstractLoadBalancerRule> furionServiceLoader;

    public RouteFilter() {
        furionServiceLoader = FurionServiceLoader.load(AbstractLoadBalancerRule.class);
    }

    //todo: for test
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

//    @Override
//    public Object run() throws FurionException {
//        getCurrentExclusiveOwnerChannel();
//        Long requestId = getCurrentExclusiveOwnerRequestId();
//        RequestCommand command = new RequestCommand();
//        command.setRequestId(requestId);
//        command.setRequest(RequestLRUContext.get(requestId).getRequest());
//        HttpNetWork httpNetWork = HttpNetFactory.fetchProcessor(ProtocolType.NETTY, new Server("127.0.0.1", 8080));
//        FurionResponse response = (FurionResponse) httpNetWork.send(command);
////        ChannelFuture future = channel.writeAndFlush();
//        writeAndFlush(response.getResponse());
//        return null;
//    }

    public Object run() throws FurionException {
        Long requestId = getCurrentExclusiveOwnerRequestId();
        FullHttpRequest fullHttpRequest = RequestLRUContext.get(requestId).getRequest();
        String uri = fullHttpRequest.uri();
        if (uri.startsWith("/config")) {

        }
        String u = uri.replace("/spi", "");
        if (u.endsWith("ico"))
            return null;
        String urlOrServiceId = getServiceId(u);

        Server server;
        if (urlOrServiceId.startsWith("http://")) {
            //静态路由
            String url = urlOrServiceId.substring(urlOrServiceId.indexOf("http://") + 1);
            server = new Server(url.split(":")[0], Integer.valueOf(url.split(":")[1]));
        } else {
            //serviceId
            server = furionServiceLoader.first().choose(urlOrServiceId);
        }
        RequestCommand command = RequestLRUContext.get(requestId).builder();
        fullHttpRequest.headers().set("Host", server.getHost().concat(":").concat(String.valueOf(server.getPort())));
        fullHttpRequest.setUri(getTargetUrl(u));
        RequestLRUContext.add(requestId, command);
        fullHttpRequest.headers().set(REQUEST_ID, requestId);
        HttpNetWork httpNetWork = HttpNetFactory.fetchProcessor(ProtocolType.NETTY, server);
        FurionResponse response = (FurionResponse) httpNetWork.send(command);
        ChannelFuture future = writeAndFlush(response.getResponse());
        return null;
    }


    //mock
    public FurionProperties getFurionProperties() {
        FurionProperties furionProperties = new FurionProperties();
        Map<String, List<FurionProperties.FurionRoute>> map = new HashMap<>();
        List<FurionProperties.FurionRoute> furionRouteList = new ArrayList<>();
        FurionProperties.FurionRoute furionRoute = new FurionProperties.FurionRoute("/solar-service-a/**", "solar-service-a");
        furionRouteList.add(furionRoute);
        map.put("/solar-service-a", furionRouteList);
        furionProperties.setRoutes(map);
        return furionProperties;
    }

    public String getServiceId(String targetUrl) {
        try {
            String urlPreffix = targetUrl.substring(0, targetUrl.indexOf("/", 1));
            List<FurionProperties.FurionRoute> list = getFurionProperties().getRoutes().get(urlPreffix);
            for (FurionProperties.FurionRoute furionRoute : list) {
                if (UrlMatchUtil.isMatch(targetUrl, furionRoute.getPath())) {
                    return StringUtil.isNullOrEmpty(furionRoute.getServiceId()) ? furionRoute.getUrl() : furionRoute.getServiceId();
                }
            }
        } catch (Exception e) {
        }
        System.out.println(targetUrl);
        return targetUrl.substring(1, targetUrl.indexOf("/", 1));
    }

    public String getTargetUrl(String targetUrl) {
        try {
            String urlPreffix = targetUrl.substring(0, targetUrl.indexOf("/", 1));
            List<FurionProperties.FurionRoute> list = getFurionProperties().getRoutes().get(urlPreffix);
            for (FurionProperties.FurionRoute furionRoute : list) {
                if (UrlMatchUtil.isMatch(targetUrl, furionRoute.getPath())) {
                    return UrlMatchUtil.transUrl(targetUrl, furionRoute.getPath());
                }
            }
        } catch (Exception e) {
        }
        return targetUrl.substring(targetUrl.indexOf("/", 1));
    }

    public void updateConfig(String uri, FullHttpRequest fullHttpRequest) {
        switch (uri) {
            case "config/furion":
                break;
            case "config/route":
                break;

        }

    }

}
