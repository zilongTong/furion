package org.furion.core.protocol.server;

import org.apache.commons.lang3.time.StopWatch;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.enumeration.ProtocolType;
import org.furion.core.utils.FurionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import java.util.Properties;


import static org.furion.core.protocol.server.FurionServerNetWork.MAX_CHUNK_SIZE_DEFAULT;
import static org.furion.core.protocol.server.FurionServerNetWork.MAX_HEADER_SIZE_DEFAULT;
import static org.furion.core.protocol.server.FurionServerNetWork.MAX_INITIAL_LINE_LENGTH_DEFAULT;

public class FurionDefaultHttpServerBootstrap implements FurionHttpServerBootstrap {

    private final static Logger LOG = LoggerFactory.getLogger(FurionDefaultHttpServerBootstrap.class);

    private String name = "furion-gateway";
    private ServerGroup serverGroup = null;
    private ProtocolType protocolType = ProtocolType.TCP;
    private InetSocketAddress requestedAddress;
    private int port = 8080;
    private boolean allowLocalOnly = true;

    private boolean authenticateSslClients = true;

    private boolean transparent = false;
    private int idleConnectionTimeout = 70;
    private int connectTimeout = 40000;
    private long readThrottleBytesPerSecond;
    private long writeThrottleBytesPerSecond;
    private InetSocketAddress localAddress;
    private String gatewayAlias;
    private int clientToGatewayAcceptorThreads = ServerGroup.DEFAULT_INCOMING_ACCEPTOR_THREADS;
    private int clientToGatewayWorkerThreads = ServerGroup.DEFAULT_INCOMING_WORKER_THREADS;
    private int gatewayToServerWorkerThreads = ServerGroup.DEFAULT_OUTGOING_WORKER_THREADS;
    private int maxInitialLineLength = MAX_INITIAL_LINE_LENGTH_DEFAULT;
    private int maxHeaderSize = MAX_HEADER_SIZE_DEFAULT;
    private int maxChunkSize = MAX_CHUNK_SIZE_DEFAULT;
    private boolean allowRequestToOriginServer = false;

    FurionDefaultHttpServerBootstrap() {
    }

    private FurionDefaultHttpServerBootstrap(
            ServerGroup serverGroup,
            ProtocolType protocolType,
            InetSocketAddress requestedAddress,
            boolean authenticateSslClients,

            boolean transparent, int idleConnectionTimeout,

            long readThrottleBytesPerSecond,
            long writeThrottleBytesPerSecond,
            InetSocketAddress localAddress,
            String gatewayAlias,
            int maxInitialLineLength,
            int maxHeaderSize,
            int maxChunkSize,
            boolean allowRequestToOriginServer) {
        this.serverGroup = serverGroup;
        this.protocolType = protocolType;
        this.requestedAddress = requestedAddress;
        this.port = requestedAddress.getPort();

        this.authenticateSslClients = authenticateSslClients;

        this.transparent = transparent;
        this.idleConnectionTimeout = idleConnectionTimeout;

        this.connectTimeout = connectTimeout;

        this.readThrottleBytesPerSecond = readThrottleBytesPerSecond;
        this.writeThrottleBytesPerSecond = writeThrottleBytesPerSecond;
        this.localAddress = localAddress;
        this.gatewayAlias = gatewayAlias;
        this.maxInitialLineLength = maxInitialLineLength;
        this.maxHeaderSize = maxHeaderSize;
        this.maxChunkSize = maxChunkSize;
        this.allowRequestToOriginServer = allowRequestToOriginServer;
    }

    public FurionDefaultHttpServerBootstrap(Properties props) {
        this.withUseDnsSec(FurionUtils.extractBooleanDefaultFalse(
                props, "dnssec"));
        this.transparent = FurionUtils.extractBooleanDefaultFalse(
                props, "transparent");
        this.idleConnectionTimeout = FurionUtils.extractInt(props,
                "idle_connection_timeout");
        this.connectTimeout = FurionUtils.extractInt(props,
                "connect_timeout", 0);
        this.maxInitialLineLength = FurionUtils.extractInt(props,
                "max_initial_line_length", MAX_INITIAL_LINE_LENGTH_DEFAULT);
        this.maxHeaderSize = FurionUtils.extractInt(props,
                "max_header_size", MAX_HEADER_SIZE_DEFAULT);
        this.maxChunkSize = FurionUtils.extractInt(props,
                "max_chunk_size", MAX_CHUNK_SIZE_DEFAULT);
    }

    @Override
    public FurionHttpServerBootstrap withName(String name) {
        this.name = name;
        return this;
    }


    public FurionHttpServerBootstrap withTransportProtocol(
            ProtocolType protocol) {
        this.protocolType = protocol;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withAddress(InetSocketAddress address) {
        this.requestedAddress = address;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withPort(int port) {
        this.requestedAddress = null;
        this.port = port;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withNetworkInterface(InetSocketAddress inetSocketAddress) {
        this.localAddress = inetSocketAddress;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withGatewayAlias(String alias) {
        this.gatewayAlias = alias;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withAllowLocalOnly(
            boolean allowLocalOnly) {
        this.allowLocalOnly = allowLocalOnly;
        return this;
    }

    @Override
    @Deprecated
    public FurionHttpServerBootstrap withListenOnAllAddresses(boolean listenOnAllAddresses) {
        LOG.warn("withListenOnAllAddresses() is deprecated and will be removed in a future release. Use withNetworkInterface().");
        return this;
    }


    @Override
    public FurionHttpServerBootstrap withAuthenticateSslClients(
            boolean authenticateSslClients) {
        this.authenticateSslClients = authenticateSslClients;
        return this;
    }


    @Override
    public FurionHttpServerBootstrap withTransparent(
            boolean transparent) {
        this.transparent = transparent;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withIdleConnectionTimeout(
            int idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withConnectTimeout(
            int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }


    @Override
    public FurionHttpServerBootstrap withThrottling(long readThrottleBytesPerSecond, long writeThrottleBytesPerSecond) {
        this.readThrottleBytesPerSecond = readThrottleBytesPerSecond;
        this.writeThrottleBytesPerSecond = writeThrottleBytesPerSecond;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
        return this;
    }

    @Override
    public FurionHttpServerBootstrap withAllowRequestToOriginServer(boolean allowRequestToOriginServer) {
        this.allowRequestToOriginServer = allowRequestToOriginServer;
        return this;
    }

    @Override
    public FurionServerNetWork start() {
        return build().start();
    }

    @Override
    public FurionHttpServerBootstrap withThreadPoolConfiguration(ThreadPoolConfiguration configuration) {
        this.clientToGatewayAcceptorThreads = configuration.getAcceptorThreads();
        this.clientToGatewayWorkerThreads = configuration.getClientToGatewayWorkerThreads();
        this.gatewayToServerWorkerThreads = configuration.getGatewayToServerWorkerThreads();
        return this;
    }

    private FurionServerNetWork build() {
        final ServerGroup serverGroup;

        if (this.serverGroup != null) {
            serverGroup = this.serverGroup;
        } else {
            serverGroup = new ServerGroup(name, clientToGatewayAcceptorThreads, clientToGatewayWorkerThreads, gatewayToServerWorkerThreads);
        }

        return new FurionServerNetWork(
                maxInitialLineLength,
                maxHeaderSize,
                maxChunkSize,
                connectTimeout,
                idleConnectionTimeout,
                serverGroup,
                protocolType,
                determineListenAddress(),
                localAddress);
    }

    private InetSocketAddress determineListenAddress() {
        if (requestedAddress != null) {
            return requestedAddress;
        } else {
            // Binding only to localhost can significantly improve the
            // security of the Gateway.
            if (allowLocalOnly) {
                return new InetSocketAddress("127.0.0.1", port);
            } else {
                return new InetSocketAddress(port);
            }
        }
    }

    public FurionGatewayContext run(String... args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        FurionGatewayContext context = new FurionGatewayContext(args);
        try {
//            context.refresh();
            this.withPort(8080).start();
            stopWatch.stop();
            return context;
        } catch (Throwable var9) {
            throw new IllegalStateException(var9);
        }
    }

    @Override
    public FurionHttpServerBootstrap withTransportProtocol(com.sun.deploy.net.protocol.ProtocolType protocol) {
        return null;
    }

    @Override
    public FurionHttpServerBootstrap withUseDnsSec(boolean useDnsSec) {
        return null;
    }


    public static void main(String[] args) {
        new FurionDefaultHttpServerBootstrap().run(args);
    }

}
