package org.furion.core.protocol.client.http;

import io.netty.util.HashedWheelTimer;
import org.furion.core.bean.eureka.Server;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.protocol.client.NettyClientNetWork;
import org.furion.core.protocol.client.OKHttpClientNetWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpNetFactory {

//    private static Map<ProtocolType, HttpNetWork> typeHttpNetWorkMap = new ConcurrentHashMap<>();

    private static final HashedWheelTimer timer = new HashedWheelTimer();


    static Map<String,HttpNetWork> map = new ConcurrentHashMap<>();

    /**
     * 非单例
     *
     * @param type
     * @return
     */
    public static HttpNetWork fetchProcessor(ProtocolType type, Server server) {
        String key = server.getHost()+":"+server.getPort();
        if(map.containsKey(key))
            return map.get(key);
        HttpNetWork httpNetWork = new OKHttpClientNetWork();
        if (type.equals(ProtocolType.NETTY)) {
            httpNetWork = new NettyClientNetWork(timer, server);
        }
        if (type.equals(ProtocolType.OK_HTTP)) {
            httpNetWork = new OKHttpClientNetWork();
        }
        map.put(key,httpNetWork);
        return httpNetWork;
    }

//    public static HttpNetWork fetchProcessorSingleton(ProtocolType type) {
//
//        return typeHttpNetWorkMap.get(type);
//
//    }

}
