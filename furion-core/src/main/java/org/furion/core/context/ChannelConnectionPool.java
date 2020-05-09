package org.furion.core.context;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;
import org.furion.core.bean.eureka.Server;
import org.furion.core.protocol.server.FurionSocketChannel;
import org.furion.core.utils.StringUtils;

import java.util.Vector;

@Data
public class ChannelConnectionPool {

    private final Bootstrap bootstrap;

    private int incrementalConnections;// 连接池自动增加的大小
    private int maxTotalConnections; // 总连接池最大的大小

    private int initialConnectionsPerHost; // 连接池的初始大小
    private int maxConnectionsPerHost; // 连接池最大的大小

    private final int DEFAULT_INCREMENTAL_CONNECTIONS = 5;// 默认连接池自动增加的大小
    private final int DEFAULT_MAX_TOTAL_CONNECTIONS = 2048; // 默认连接池最大的大小

    private int DEFAULT_INITIAL_CONNECTIONS_PER_HOST = 50; // 连接池的初始大小
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


    public SocketChannel getConnect(Server server) {
        SocketChannel channel = ClientChannelLRUContext.get(getKey(server));
        if (channel == null) {
            if (ClientChannelLRUContext.getClientConnected(getKey(server)).size() > 0) {
                if (!incrementConnections(server)) {
                    return null;
                }
            } else {
                createPool(server);
            }
            return getConnect(server);
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
        Vector<FurionSocketChannel> connections = new Vector<>();
        if (!StringUtils.isEmpty(server.getId())) {
            for (int i = 0; i < DEFAULT_INITIAL_CONNECTIONS_PER_HOST; i++) {
                connections.addElement(connect(server));
            }
            ClientChannelLRUContext.add(server.getId(), connections);

        }
    }

    private String getKey(Server server) {
        if (StringUtils.isEmpty(server.getId())) {
            return server.getHost().concat(":").concat(String.valueOf(server.getPort()));
        }
        return server.getId();
    }


    private boolean incrementConnections(Server server) {
        String key = getKey(server);
        Vector<FurionSocketChannel> socketChannels = ClientChannelLRUContext.getClientConnected(key);

        for (int x = 0; x < DEFAULT_INCREMENTAL_CONNECTIONS; x++) {

            if (this.DEFAULT_MAX_CONNECTIONS_PER_HOST > 0
                    && ClientChannelLRUContext.getClientConnected(getKey(server)).size() >= this.DEFAULT_MAX_CONNECTIONS_PER_HOST) {
                return false;
            }
            try {
                socketChannels.addElement(connect(server));
                ClientChannelLRUContext.add(key, socketChannels);
            } catch (Exception e) {
                System.out.println(" 创建连接失败！ " + e.getMessage());
            }
        }
        return true;
    }

}

