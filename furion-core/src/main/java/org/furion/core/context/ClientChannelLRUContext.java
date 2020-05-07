package org.furion.core.context;


import io.netty.channel.socket.SocketChannel;
import org.furion.core.protocol.server.FurionSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ClientChannelLRUContext {

    private static ConcurrentLRUHashMap<String/**ip:port**/, List<FurionSocketChannel>> socketChannelMap = new ConcurrentLRUHashMap<>(1024);
    private static ConcurrentHashMap<SocketChannel,FurionSocketChannel> isUsedChannel = new ConcurrentHashMap<>();

    public static void add(String clientId, SocketChannel channel) {
        if(!socketChannelMap.containsKey(clientId))
            socketChannelMap.put(clientId,new ArrayList<>());
        socketChannelMap.get(clientId).add(new FurionSocketChannel(channel,true));
    }

    public static SocketChannel get(String clientId) {
        List<FurionSocketChannel> furionSocketChannelList = socketChannelMap.get(clientId);
        if(furionSocketChannelList != null && !furionSocketChannelList.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(furionSocketChannelList.size());
            for (int i = 0; i < furionSocketChannelList.size(); i++) {
                FurionSocketChannel furionSocketChannel = furionSocketChannelList.get((index + i) % furionSocketChannelList.size());
                if (furionSocketChannel.isFree()) {
                    if(!furionSocketChannel.getSocketChannel().isActive()){
                        furionSocketChannelList.remove(furionSocketChannel);
                        continue;
                    }
                    furionSocketChannel.setFree(false);
                    isUsedChannel.put(furionSocketChannel.getSocketChannel(), furionSocketChannel);
                    return furionSocketChannel.getSocketChannel();
                }
            }
        }
        return null;
    }

    public static void setFree(SocketChannel socketChannel){
        if(isUsedChannel.containsKey(socketChannel)) {
            isUsedChannel.remove(socketChannel).setFree(true);
        }else {
            System.out.println("");
        }
    }

    public static synchronized void remove(SocketChannel channel) {
        if (socketChannelMap != null && socketChannelMap.size() > 0) {
            socketChannelMap.remove(channel.remoteAddress());
        }
    }

}
