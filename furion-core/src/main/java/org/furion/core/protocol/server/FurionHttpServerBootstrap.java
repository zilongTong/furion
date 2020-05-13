package org.furion.core.protocol.server;


import org.furion.core.enumeration.ProtocolType;

import java.net.InetSocketAddress;

/**
 * Configures and starter.
 */
public interface FurionHttpServerBootstrap {

    /**
     * <p>
     * Give the server a name (used for naming threads, useful for logging).
     * </p>
     *
     * <p>
     * Default = furion-gateway
     * </p>
     *
     * @param name
     * @return
     */
    FurionHttpServerBootstrap withName(String name);


    FurionHttpServerBootstrap withTransportProtocol(
            ProtocolType protocol);

    /**
     * <p>
     * Listen for incoming connections on the given address.
     * </p>
     *
     * <p>
     * Default = [bound ip]:8080
     * </p>
     *
     * @param address
     * @return
     */
    FurionHttpServerBootstrap withAddress(InetSocketAddress address);

    /**
     * <p>
     * Listen for incoming connections on the given port.
     * </p>
     *
     * <p>
     * Default = 8080
     * </p>
     *
     * @param port
     * @return
     */
    FurionHttpServerBootstrap withPort(int port);


    FurionHttpServerBootstrap withAllowLocalOnly(boolean allowLocalOnly);

    @Deprecated
    FurionHttpServerBootstrap withListenOnAllAddresses(boolean listenOnAllAddresses);


    FurionHttpServerBootstrap withAuthenticateSslClients(
            boolean authenticateSslClients);


    FurionHttpServerBootstrap withUseDnsSec(
            boolean useDnsSec);


    FurionHttpServerBootstrap withTransparent(
            boolean transparent);


    FurionHttpServerBootstrap withIdleConnectionTimeout(
            int idleConnectionTimeout);


    FurionHttpServerBootstrap withConnectTimeout(
            int connectTimeout);


    FurionHttpServerBootstrap withThrottling(long readThrottleBytesPerSecond, long writeThrottleBytesPerSecond);


    FurionHttpServerBootstrap withNetworkInterface(InetSocketAddress inetSocketAddress);

    FurionHttpServerBootstrap withMaxInitialLineLength(int maxInitialLineLength);

    FurionHttpServerBootstrap withMaxHeaderSize(int maxHeaderSize);

    FurionHttpServerBootstrap withMaxChunkSize(int maxChunkSize);


    FurionHttpServerBootstrap withAllowRequestToOriginServer(boolean allowRequestToOriginServer);


    FurionHttpServerBootstrap withGatewayAlias(String alias);


    FurionServerNetWork start();


    FurionHttpServerBootstrap withThreadPoolConfiguration(ThreadPoolConfiguration configuration);
}