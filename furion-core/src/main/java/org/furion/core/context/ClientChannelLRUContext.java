package org.furion.core.context;


import io.netty.channel.socket.SocketChannel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.furion.core.bean.eureka.Server;
import org.furion.core.enumeration.MsgType;
import org.furion.core.exception.NoChannelBindRequestException;
import org.furion.core.protocol.server.FurionSocketChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-31
 */
public class ClientChannelLRUContext {

    private static ConcurrentLRUHashMap<String/**ip:port**/, Vector<FurionSocketChannel>> socketChannelMap = new ConcurrentLRUHashMap<>(1024);

    private static ConcurrentHashMap<SocketChannel, FurionSocketChannel> isUsedChannel = new ConcurrentHashMap<>();

    public static void add(String clientId, SocketChannel channel) {
        if (!socketChannelMap.containsKey(clientId))
            socketChannelMap.put(clientId, new Vector<>());
        socketChannelMap.get(clientId).addElement(new FurionSocketChannel(channel, true));
    }

    public static void add(String clientId, Vector<FurionSocketChannel> vector) {
        if (!socketChannelMap.containsKey(clientId))
            socketChannelMap.put(clientId, vector);
//        socketChannelMap.get(clientId).addElement(new FurionSocketChannel(channel, true));
    }


    private static String getKey(Server server) {
        return server.getHost().concat(":").concat(String.valueOf(server.getPort()));
    }

    public static Vector<FurionSocketChannel> getClientConnected(String clientId) {
        return socketChannelMap.get(clientId);
    }

    public static FurionSocketChannel getRequestIdByChannel(SocketChannel channel) {
        FurionSocketChannel furionSocketChannel = isUsedChannel.get(channel);
        if (furionSocketChannel == null || furionSocketChannel.getExclusiveOwnerRequest() == null) {
            throw new NoChannelBindRequestException(channel);
        }
        return furionSocketChannel;
    }

    public static SocketChannel get(String clientId, Long requestId) {
        List<FurionSocketChannel> furionSocketChannelList = socketChannelMap.get(clientId);
        if (furionSocketChannelList != null && !furionSocketChannelList.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(furionSocketChannelList.size());
            for (int i = 0; i < furionSocketChannelList.size(); i++) {
                FurionSocketChannel furionSocketChannel = furionSocketChannelList.get((index + i) % furionSocketChannelList.size());
                if (furionSocketChannel.isFree() && furionSocketChannel.getExclusiveOwnerRequest() == null) {
                    if (!furionSocketChannel.getSocketChannel().isActive()) {
                        furionSocketChannelList.remove(furionSocketChannel);
                        continue;
                    }
                    furionSocketChannel.setFree(false);
                    furionSocketChannel.setExclusiveOwnerRequest(requestId);
                    isUsedChannel.put(furionSocketChannel.getSocketChannel(), furionSocketChannel);
                    return furionSocketChannel.getSocketChannel();
                }
            }
        }
        return null;
    }


    public static boolean setBusy(SocketChannel socketChannel, Server sever, Long id, MsgType msgType) {
        if (isUsedChannel.containsKey(socketChannel)) {
        } else {
            String key = getKey(sever);
            Vector<FurionSocketChannel> vector = getClientConnected(key);
            if (CollectionUtils.isNotEmpty(vector)) {
                for (FurionSocketChannel f : vector) {
                    if (f.getSocketChannel() == socketChannel) {
                        vector.remove(f);
                        f.setFree(false);
                        f.setExclusiveOwnerRequest(id);
                        f.setMsgType(msgType);
                        vector.addElement(f);
                        isUsedChannel.put(socketChannel, f);
                    }
                }
                socketChannelMap.put(key, vector);
            }
        }
        return false;
    }


    public static void setFree(SocketChannel socketChannel) {
        if (isUsedChannel.containsKey(socketChannel)) {
            FurionSocketChannel furionSocketChannel = isUsedChannel.get(socketChannel);
            isUsedChannel.remove(socketChannel);
            furionSocketChannel.releaseChannel();
        } else {
            System.out.println("");
        }
    }

    public static synchronized void remove(SocketChannel channel) {
        if (socketChannelMap != null && socketChannelMap.size() > 0) {
            socketChannelMap.remove(channel.remoteAddress());
        }
    }

}
