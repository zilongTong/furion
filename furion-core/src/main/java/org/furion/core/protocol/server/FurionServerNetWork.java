package org.furion.core.protocol.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.UnknownTransportProtocolException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import java.util.Properties;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


public class FurionServerNetWork implements FurionHttpServer {

    private final static Logger LOG = LoggerFactory.getLogger(FurionServerNetWork.class);


    /**
     * traffic
     */
//    public static final long TRAFFIC_SHAPING_CHECK_INTERVAL_MS = 250L;
    private volatile GlobalTrafficShapingHandler globalTrafficShapingHandler;

    public static final int MAX_INITIAL_LINE_LENGTH_DEFAULT = 8192;
    public static final int MAX_HEADER_SIZE_DEFAULT = 8192 * 2;
    public static final int MAX_CHUNK_SIZE_DEFAULT = 8192 * 2;


    private int maxInitialLineLength;
    private int maxHeaderSize;
    private int maxChunkSize;
    private boolean allowRequestsToOriginServer;
    private volatile int connectTimeout;
    private volatile int idleConnectionTimeout;
    private final ServerGroup serverGroup;

    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }

    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }

    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }

    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }

    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }

    private final ProtocolType protocolType;
    private final InetSocketAddress requestedAddress;
    /*
     * The actual address to which the server is bound. May be different from the requestedAddress in some circumstances,
     * for example when the requested port is 0.
     */
    private volatile InetSocketAddress localAddress;
    private volatile InetSocketAddress boundAddress;

    public FurionServerNetWork(GlobalTrafficShapingHandler globalTrafficShapingHandler, int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean allowRequestsToOriginServer, ServerGroup serverGroup, ProtocolType protocolType, InetSocketAddress requestedAddress, InetSocketAddress localAddress, InetSocketAddress boundAddress) {
        this.globalTrafficShapingHandler = globalTrafficShapingHandler;
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
        this.allowRequestsToOriginServer = allowRequestsToOriginServer;
        this.serverGroup = serverGroup;
        this.protocolType = protocolType;
        this.requestedAddress = requestedAddress;
        this.localAddress = localAddress;
        this.boundAddress = boundAddress;
    }

    public FurionServerNetWork(ServerGroup serverGroup, ProtocolType protocolType, InetSocketAddress requestedAddress, InetSocketAddress localAddress) {
        this.serverGroup = serverGroup;
        this.protocolType = protocolType;
        this.requestedAddress = requestedAddress;
        this.localAddress = localAddress;
    }

    public FurionServerNetWork(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, int connectTimeout, int idleConnectionTimeout, ServerGroup serverGroup, ProtocolType protocolType, InetSocketAddress requestedAddress, InetSocketAddress localAddress) {
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
        this.allowRequestsToOriginServer = allowRequestsToOriginServer;
        this.connectTimeout = connectTimeout;
        this.idleConnectionTimeout = idleConnectionTimeout;
        this.serverGroup = serverGroup;
        this.protocolType = protocolType;
        this.requestedAddress = requestedAddress;
        this.localAddress = localAddress;
    }

    private final AtomicBoolean stopped = new AtomicBoolean(false);


    private final ChannelGroup allChannels = new DefaultChannelGroup("furion-gateway-Server", GlobalEventExecutor.INSTANCE);


    private final Thread jvmShutdownHook = new Thread(new Runnable() {
        @Override
        public void run() {
            abort();
        }
    }, "furion-gateway-JVM-shutdown-hook");

    public static FurionHttpServerBootstrap bootstrap() {
        return new FurionDefaultHttpServerBootstrap();
    }


    public static FurionHttpServerBootstrap bootstrapFromFile(String path) {
        final File propsFile = new File(path);
        Properties props = new Properties();

        if (propsFile.isFile()) {
            try (InputStream is = new FileInputStream(propsFile)) {
                props.load(is);
            } catch (final IOException e) {
                LOG.warn("Could not load props file?", e);
            }
        }

        return new FurionDefaultHttpServerBootstrap(props);
    }


    @Override
    public void stop() {
        doStop(true);
    }

    @Override
    public void abort() {
        doStop(false);
    }


    protected void doStop(boolean graceful) {
        // only stop the server if it hasn't already been stopped
        if (stopped.compareAndSet(false, true)) {
            if (graceful) {
                LOG.info("Shutting down furion-gateway  gracefully");
            } else {
                LOG.info("Shutting down furion-gateway  immediately (non-graceful)");
            }

            closeAllChannels(graceful);

            serverGroup.unregisterProxyServer(this, graceful);

            // remove the shutdown hook that was added when the proxy was started, since it has now been stopped
            try {
                Runtime.getRuntime().removeShutdownHook(jvmShutdownHook);
            } catch (IllegalStateException e) {
                // ignore -- IllegalStateException means the VM is already shutting down
            }

            LOG.info("Done shutting down furion gateway");
        }
    }


    public void registerChannel(Channel channel) {
        allChannels.add(channel);
    }

    public ChannelGroup getAllChannels() {
        return allChannels;
    }

    protected void closeAllChannels(boolean graceful) {
        LOG.info("Closing all channels " + (graceful ? "(graceful)" : "(non-graceful)"));

        ChannelGroupFuture future = allChannels.close();

        // if this is a graceful shutdown, log any channel closing failures. if this isn't a graceful shutdown, ignore them.
        if (graceful) {
            try {
                future.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                LOG.warn("Interrupted while waiting for channels to shut down gracefully.");
            }

            if (!future.isSuccess()) {
                for (ChannelFuture cf : future) {
                    if (!cf.isSuccess()) {
                        LOG.info("Unable to close channel.  Cause of failure for {} is {}", cf.channel(), cf.cause());
                    }
                }
            }
        }
    }

    public FurionServerNetWork start() {
        if (!serverGroup.isStopped()) {
            LOG.info("Starting gateway at address: " + this.requestedAddress);
            serverGroup.registerProxyServer(this);
            doStart();
        } else {
            throw new IllegalStateException("Attempted to start gateway, but  server group is already stopped");
        }
        return this;
    }


    private void doStart() {
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(
                serverGroup.getClientToGatewayAcceptorPoolForTransport(protocolType),
                serverGroup.getClientToGatewayWorkerPoolForTransport(protocolType));
        switch (protocolType) {
            case TCP:
                LOG.info("gateway listening with TCP transport");
                serverBootstrap.channel(NioServerSocketChannel.class);
                break;
            case UDT:
                LOG.info("gateway listening with UDT transport");
                serverBootstrap.channelFactory(NioUdtProvider.BYTE_ACCEPTOR)
                        .option(ChannelOption.SO_BACKLOG, 10)
                        .option(ChannelOption.SO_REUSEADDR, true);
                break;
            default:
                throw new UnknownTransportProtocolException(protocolType);
        }
        serverBootstrap.childHandler(new FurionServerChannelInitializer(FurionServerNetWork.this));
        ChannelFuture future = serverBootstrap.bind(requestedAddress)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        if (future.isSuccess()) {
                            registerChannel(future.channel());
                        }
                    }
                }).awaitUninterruptibly();

        Throwable cause = future.cause();
        if (cause != null) {
            throw new RuntimeException(cause);
        }
        this.boundAddress = ((InetSocketAddress) future.channel().localAddress());
        LOG.info("furion gateway started at address: " + this.boundAddress);
        Runtime.getRuntime().addShutdownHook(jvmShutdownHook);
    }

    @Override
    public int getIdleConnectionTimeout() {
        return 0;
    }

    @Override
    public void setIdleConnectionTimeout(int idleConnectionTimeout) {

    }

    @Override
    public int getConnectTimeout() {
        return 0;
    }

    @Override
    public void setConnectTimeout(int connectTimeoutMs) {

    }

    @Override
    public FurionHttpServerBootstrap clone() {
        return null;
    }

    @Override
    public InetSocketAddress getListenAddress() {
        return null;
    }

    @Override
    public void setThrottle(long readThrottleBytesPerSecond, long writeThrottleBytesPerSecond) {

    }



}
