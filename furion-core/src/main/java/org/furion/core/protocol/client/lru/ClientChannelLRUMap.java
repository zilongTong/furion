package org.furion.core.protocol.client.lru;


import io.netty.channel.socket.SocketChannel;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ClientChannelLRUMap {

    private static ConcurrentLRUHashMap<String, SocketChannel> socketChannelMap = new ConcurrentLRUHashMap<>(1024);

    public static void add(String clientId, SocketChannel channel) {
        socketChannelMap.put(clientId, channel);
    }

    public static SocketChannel get(String clientId) {
        return socketChannelMap.get(clientId);
    }

    public static void remove(SocketChannel channel) {
        socketChannelMap.entrySet.stream().forEach(e -> {
            if (e.getValue().equals(channel)) {
                socketChannelMap.remove(channel);
            }
        });
    }


}
