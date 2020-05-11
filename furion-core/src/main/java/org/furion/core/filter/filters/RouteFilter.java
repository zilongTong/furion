package org.furion.core.filter.filters;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.StringUtil;
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


        //url大致分为配置类、监控类和普通请求
        if(uri.endsWith("ico")){//过滤favicon.ico请求
            return null;
        }else if (uri.startsWith(Constants.CONFIG_PATH)) {//处理配置请求
            writeAndFlush(updateConfig(uri,fullHttpRequest));
        }else if (uri.startsWith(Constants.MONITOR_PATH)){//处理监控请求

        }else {//处理一般请求
            uri = uri.replaceAll(furionProperties.getPrefix(),"");//替换通用前缀
            String urlOrServiceId = getServiceId(uri);
            Server server;
            if (urlOrServiceId.startsWith(Constants.HTTP_PREFIX)) {
                //静态路由
                String url = urlOrServiceId.substring(urlOrServiceId.indexOf(Constants.HTTP_PREFIX) + 1);
                server = new Server(url.split(":")[0], Integer.valueOf(url.split(":")[1]));
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
        }
        return null;
    }


    public String getServiceId(String targetUrl) {
        try {
            String urlPreffix = targetUrl.substring(0, indexOfSecond(targetUrl));
            List<FurionProperties.FurionRoute> list = FurionGatewayContext.getInstance().getFurionProperties().getRoutes().get(urlPreffix);
            for (FurionProperties.FurionRoute furionRoute : list) {
                if (UrlMatchUtil.isMatch(targetUrl, furionRoute.getPath())) {
                    return StringUtil.isNullOrEmpty(furionRoute.getServiceId()) ? furionRoute.getUrl() : furionRoute.getServiceId();
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

    public FullHttpResponse updateConfig(String uri, FullHttpRequest fullHttpRequest) {
        try {
            ByteBuf byteBuf = fullHttpRequest.content();
            byte[] src = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(src);
            switch (uri) {
                case Constants.CONFIG_PATH_SYSTEM:
                    break;
                case Constants.CONFIG_PATH_FURION:
                    Properties properties = JsonUtil.getObject(src, Properties.class);
                    propertiesManager.refresh(PropertiesSource.NET,properties);
                    break;
                case Constants.CONFIG_PATH_FIELTER:
                    String filterPath = RouteFilter.class.getResource("/filter").getPath()+"/";
                    String fileName = getJavaFileName(new String(src));
                    if(!StringUtil.isNullOrEmpty(fileName)){
                        try(FileOutputStream fos = new FileOutputStream(filterPath+fileName)){
                            fos.write(src);
                            fos.flush();
                        }
                    }
                    break;
                default:
                    break;

            }
            return getResponse(HttpResponseStatus.OK,"更新配置成功");
        }catch (Exception e){
            System.out.println("更新配置失败"+e);
            return getResponse(HttpResponseStatus.BAD_REQUEST,"更新配置失败");
        }

    }

    private String getJavaFileName(String src){
        int startIndex = src.indexOf("class")+5;
        int endIndex = src.indexOf("extends");
        if(startIndex > 0 && endIndex > 0 && startIndex<=endIndex){
            return src.substring(startIndex,endIndex).replaceAll(" ","").concat(".java");
        }else {
            System.out.println("java源文件格式异常");
            return "";
        }
    }

    private FullHttpResponse getResponse(HttpResponseStatus httpResponseStatus,String msg){
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        fullHttpResponse.content().writeBytes(msg.getBytes());
        return fullHttpResponse;
    }

    private int indexOfSecond(String url){
        return url.indexOf("/", 1);
    }

    public static void main(String[] args) {
        System.out.println(new RouteFilter().getJavaFileName("public class RouteFilter extends FurionFilter {public class RouteFilter extends FurionFilter {"));
    }

}
