package org.furion.core.context;


import io.netty.channel.socket.SocketChannel;

import java.util.Map;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ClientChannelLRUMap {

    private static ConcurrentLRUHashMap<String, SocketChannel> socketChannelMap = new ConcurrentLRUHashMap<String, SocketChannel>(1024);

    public static void add(String clientId, SocketChannel channel) {
        socketChannelMap.put(clientId, channel);
    }

    public static SocketChannel get(String clientId) {
        return socketChannelMap.get(clientId);
    }

    public static synchronized void remove(SocketChannel channel) {
        if (socketChannelMap != null && socketChannelMap.size() > 0) {
            for (Map.Entry<String, SocketChannel> entry : socketChannelMap.entrySet()) {
                if (entry.getValue() == channel) {
                    socketChannelMap.remove(entry.getKey());
                }
            }
        }
    }

}
