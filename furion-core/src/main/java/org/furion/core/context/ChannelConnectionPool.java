package org.furion.core.context;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.furion.core.bean.eureka.Server;
import org.furion.core.protocol.client.FurionClientChannelInitializer;
import org.furion.core.protocol.server.FurionSocketChannel;
import org.furion.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class ChannelConnectionPool {

    private final Bootstrap bootstrap;

    private int incrementalConnections;// 连接池自动增加的大小
    private int maxTotalConnections; // 总连接池最大的大小

    private int initialConnectionsPerHost; // 连接池的初始大小
    private int maxConnectionsPerHost; // 连接池最大的大小

    private final int DEFAULT_INCREMENTAL_CONNECTIONS = 5;// 默认连接池自动增加的大小
    private final int DEFAULT_MAX_TOTAL_CONNECTIONS = 2048; // 默认连接池最大的大小

    private int DEFAULT_INITIAL_CONNECTIONS_PER_HOST = 5; // 连接池的初始大小
    private int DEFAULT_MAX_CONNECTIONS_PER_HOST = 200; // 连接池最大的大小

    public ChannelConnectionPool(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.initialConnectionsPerHost = DEFAULT_INITIAL_CONNECTIONS_PER_HOST;
        this.incrementalConnections = DEFAULT_INCREMENTAL_CONNECTIONS;
        this.maxTotalConnections = DEFAULT_MAX_TOTAL_CONNECTIONS;
    }

    public ChannelConnectionPool(Bootstrap bootstrap, int initialConnections, int incrementalConnections, int maxConnections) {
        this.bootstrap = bootstrap;
        this.initialConnectionsPerHost = initialConnections;
        this.incrementalConnections = incrementalConnections;
        this.maxTotalConnections = maxConnections;
    }


    public SocketChannel getConnect(Server server,Long requestId) {
        SocketChannel channel = ClientChannelLRUContext.get(getKey(server),requestId);
        if (channel == null) {
            if (CollectionUtils.isNotEmpty(ClientChannelLRUContext.getClientConnected(getKey(server)))) {
                if (!incrementConnections(server)) {
                    return null;
                }
            } else {
                createPool(server);
            }
            return getConnect(server,requestId);
        }
        return channel;
    }


    private FurionSocketChannel connect(Server server) {
        ChannelFuture future = null;
        try {
            future = bootstrap.connect(server.getHost(), server.getPort()).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new FurionSocketChannel((SocketChannel) future.channel(), true);
    }

    public synchronized void createPool(Server server) {
        List<FurionSocketChannel> connections = new ArrayList<>();
        if (!StringUtils.isEmpty(server.getId())) {
            for (int i = 0; i < DEFAULT_INITIAL_CONNECTIONS_PER_HOST; i++) {
                connections.add(connect(server));
            }
            CopyOnWriteArrayList list = new CopyOnWriteArrayList(connections);
            ClientChannelLRUContext.add(server.getId(), list);

        }
    }

    private String getKey(Server server) {
        return server.getHost().concat(":").concat(String.valueOf(server.getPort()));
    }


    private boolean incrementConnections(Server server) {
        String key = getKey(server);
        CopyOnWriteArrayList<FurionSocketChannel> socketChannels = ClientChannelLRUContext.getClientConnected(key);

        List<FurionSocketChannel> furionSocketChannelList = new ArrayList<>();
        for (int x = 0; x < DEFAULT_INCREMENTAL_CONNECTIONS; x++) {

            if (this.DEFAULT_MAX_CONNECTIONS_PER_HOST > 0
                    && ClientChannelLRUContext.getClientConnected(getKey(server)).size() >= this.DEFAULT_MAX_CONNECTIONS_PER_HOST) {
                return false;
            }
            try {
                furionSocketChannelList.add(connect(server));
//                ClientChannelLRUContext.add(key, socketChannels);
            } catch (Exception e) {
                System.out.println(" 创建连接失败！ " + e.getMessage());
            }
        }
        if(!furionSocketChannelList.isEmpty())
            socketChannels.addAll(furionSocketChannelList);
        return true;
    }

}

