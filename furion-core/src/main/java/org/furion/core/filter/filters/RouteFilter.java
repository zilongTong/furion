package org.furion.core.filter.filters;


import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThreadLocalRandom;
import org.furion.core.bean.eureka.Server;
import org.furion.core.constants.Constants;
import org.furion.core.context.*;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.enumeration.PropertiesSource;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.FurionException;
import org.furion.core.filter.FurionFilter;
import org.furion.core.protocol.client.http.HttpNetFactory;
import org.furion.core.protocol.client.http.HttpNetWork;
import org.furion.core.ribbon.AbstractLoadBalancerRule;
import org.furion.core.utils.FurionServiceLoader;
import org.furion.core.utils.JsonUtil;
import org.furion.core.utils.UrlMatchUtil;

import java.io.FileOutputStream;
import java.util.*;

import static org.furion.core.constants.Constants.REQUEST_ID;


public class RouteFilter extends FurionFilter {

    FurionServiceLoader<AbstractLoadBalancerRule> furionServiceLoader;
    PropertiesManager propertiesManager;
    FurionProperties furionProperties;

    public RouteFilter() {
        furionServiceLoader = FurionServiceLoader.load(AbstractLoadBalancerRule.class);
        propertiesManager = FurionGatewayContext.getInstance().getPropertiesManager();
        furionProperties = FurionGatewayContext.getInstance().getFurionProperties();
    }

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


    public Object run() throws FurionException {
        Long requestId = getCurrentExclusiveOwnerRequestId();
        FullHttpRequest fullHttpRequest = RequestLRUContext.get(requestId).getRequest();
        String uri = fullHttpRequest.uri();

        //处理一般请求
        uri = uri.replaceAll(furionProperties.getPrefix(),"");//替换通用前缀
        String urlOrServiceId = getServiceId(uri);
        Server server;
        if (urlOrServiceId.startsWith(Constants.HTTP_PREFIX)) {
            //静态路由
            String url = urlOrServiceId.substring(urlOrServiceId.indexOf(Constants.HTTP_PREFIX) + 7);
            server = new Server(url.split(":")[0], Integer.valueOf(url.split(":")[1]));
            System.out.println("****"+url+"****");
        } else {
            //serviceId
            server = furionServiceLoader.first().choose(urlOrServiceId);
            if(server == null)
                writeAndFlush(getResponse(HttpResponseStatus.BAD_REQUEST,"no server"));

        }
        RequestCommand command = RequestLRUContext.get(requestId).builder();
        fullHttpRequest.headers().set("Host", server.getHost().concat(":").concat(String.valueOf(server.getPort())));
        fullHttpRequest.setUri(getTargetUrl(uri));
        RequestLRUContext.add(requestId, command);
        fullHttpRequest.headers().set(REQUEST_ID, requestId);
        HttpNetWork httpNetWork = HttpNetFactory.fetchProcessor(ProtocolType.NETTY, server);
        FurionResponse response = (FurionResponse) httpNetWork.send(command);
        writeAndFlush(response.getResponse()).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("回写客户端成功");
                }else {
                    System.out.println("回写客户端失败");
                }
            }
        });

        return null;
    }


    public String getServiceId(String targetUrl) {
        try {
            String urlPreffix = targetUrl.substring(0, indexOfSecond(targetUrl));
            List<FurionProperties.FurionRoute> list = FurionGatewayContext.getInstance().getFurionProperties().getRoutes().get(urlPreffix);
            for (FurionProperties.FurionRoute furionRoute : list) {
                if (UrlMatchUtil.isMatch(targetUrl, furionRoute.getPath())) {
                    if(!StringUtil.isNullOrEmpty(furionRoute.getServiceId())){
                        return furionRoute.getServiceId();
                    }else {
                        List<String> ipList = furionRoute.getIpList();
                        return ipList.get(ThreadLocalRandom.current().nextInt(ipList.size()));
                    }
//                    return StringUtil.isNullOrEmpty(furionRoute.getServiceId()) ? furionRoute.getUrl() : furionRoute.getServiceId();
                }
            }
        } catch (Exception e) {
        }
        return targetUrl.substring(1, indexOfSecond(targetUrl));
    }

    public String getTargetUrl(String targetUrl) {
        try {
            String urlPreffix = targetUrl.substring(0, indexOfSecond(targetUrl));
            List<FurionProperties.FurionRoute> list = FurionGatewayContext.getInstance().getFurionProperties().getRoutes().get(urlPreffix);
            for (FurionProperties.FurionRoute furionRoute : list) {
                if (UrlMatchUtil.isMatch(targetUrl, furionRoute.getPath())) {
                    return UrlMatchUtil.transUrl(targetUrl, furionRoute.getPath());
                }
            }
        } catch (Exception e) {
        }
        return targetUrl.substring(indexOfSecond(targetUrl));
    }

    private FullHttpResponse getResponse(HttpResponseStatus httpResponseStatus, String msg) {
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        fullHttpResponse.content().writeBytes(msg.getBytes());
        return fullHttpResponse;
    }



    private int indexOfSecond(String url){
        return url.indexOf("/", 1);
    }



}
