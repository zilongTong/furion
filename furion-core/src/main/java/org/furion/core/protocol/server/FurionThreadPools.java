package org.furion.core.protocol.server;

import com.google.common.collect.ImmutableList;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.nio.channels.spi.SelectorProvider;
import java.util.List;


public class FurionThreadPools {

    private final NioEventLoopGroup clientToGatewayAcceptor;

    private final NioEventLoopGroup clientToGatewayWorkerPool;

    private final NioEventLoopGroup gatewayToServerWorkerPool;

    public FurionThreadPools(SelectorProvider selectorProvider, int incomingAcceptorThreads, int incomingWorkerThreads, int outgoingWorkerThreads, String serverGroupName, int serverGroupId) {
        clientToGatewayAcceptor = new NioEventLoopGroup(incomingAcceptorThreads, new CategorizedThreadFactory(serverGroupName, "ClientToGatewayAcceptor", serverGroupId), selectorProvider);

        clientToGatewayWorkerPool = new NioEventLoopGroup(incomingWorkerThreads, new CategorizedThreadFactory(serverGroupName, "ClientToGatewayWorker", serverGroupId), selectorProvider);
        clientToGatewayWorkerPool.setIoRatio(90);

        gatewayToServerWorkerPool = new NioEventLoopGroup(outgoingWorkerThreads, new CategorizedThreadFactory(serverGroupName, "GatewayToServerWorker", serverGroupId), selectorProvider);
        gatewayToServerWorkerPool.setIoRatio(90);
    }

    /**
     * Returns all event loops (acceptor and worker thread pools) in this pool.
     */
    public List<EventLoopGroup> getAllEventLoops() {
        return ImmutableList.<EventLoopGroup>of(clientToGatewayAcceptor, clientToGatewayWorkerPool, gatewayToServerWorkerPool);
    }

    public NioEventLoopGroup getClientToGatewayAcceptorPool() {
        return clientToGatewayAcceptor;
    }

    public NioEventLoopGroup getClientToGatewayWorkerPool() {
        return clientToGatewayWorkerPool;
    }

    public NioEventLoopGroup getGatewayToServerWorkerPool() {
        return gatewayToServerWorkerPool;
    }
}
