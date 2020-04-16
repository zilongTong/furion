package org.furion.core.protocol.client.http;

import io.netty.util.HashedWheelTimer;
import org.furion.core.bean.eureka.Server;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.protocol.client.ClientNetWork;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpNetFactory {

//    private static Map<ProtocolType, HttpNetWork> typeHttpNetWorkMap = new ConcurrentHashMap<>();

    private static final HashedWheelTimer timer = new HashedWheelTimer();


    /**
     * 非单例
     *
     * @param type
     * @return
     */
    public static HttpNetWork fetchProcessor(ProtocolType type, Server server) {
        if (type.equals(ProtocolType.NETTY)) {
            return new ClientNetWork(timer, server);
        }
//        if (type.equals(ProtocolType.NETTY)) {
//            return new ClientNetWork();
//        }
        return new ClientNetWork(timer, server);
    }

//    public static HttpNetWork fetchProcessorSingleton(ProtocolType type) {
//
//        return typeHttpNetWorkMap.get(type);
//
//    }

}
