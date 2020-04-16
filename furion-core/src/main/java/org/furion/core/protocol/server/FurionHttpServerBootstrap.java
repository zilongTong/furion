package org.furion.core.protocol.server;

import com.sun.deploy.net.protocol.ProtocolType;

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

    /**
     * <p>
     * Specify whether or not to run this proxy as a transparent proxy.
     * </p>
     *
     * <p>
     * Default = false
     * </p>
     *
     * @param transparent
     * @return
     */
    FurionHttpServerBootstrap withTransparent(
            boolean transparent);

    /**
     * <p>
     * Specify the timeout after which to disconnect idle connections, in
     * seconds.
     * </p>
     *
     * <p>
     * Default = 70
     * </p>
     *
     * @param idleConnectionTimeout
     * @return
     */
    FurionHttpServerBootstrap withIdleConnectionTimeout(
            int idleConnectionTimeout);


    FurionHttpServerBootstrap withConnectTimeout(
            int connectTimeout);


    FurionHttpServerBootstrap withThrottling(long readThrottleBytesPerSecond, long writeThrottleBytesPerSecond);

    /**
     * All outgoing-communication of the proxy-instance is goin' to be routed via the given network-interface
     *
     * @param inetSocketAddress to be used for outgoing communication
     */
    FurionHttpServerBootstrap withNetworkInterface(InetSocketAddress inetSocketAddress);

    FurionHttpServerBootstrap withMaxInitialLineLength(int maxInitialLineLength);

    FurionHttpServerBootstrap withMaxHeaderSize(int maxHeaderSize);

    FurionHttpServerBootstrap withMaxChunkSize(int maxChunkSize);


    FurionHttpServerBootstrap withAllowRequestToOriginServer(boolean allowRequestToOriginServer);


    FurionHttpServerBootstrap withGatewayAlias(String alias);

    /**
     * <p>
     * Build and starts the server.
     * </p>
     *
     * @return the newly built and started server
     */
    FurionServerNetWork start();

    /**
     * Set the configuration parameters for the proxy's thread pools.
     *
     * @param configuration thread pool configuration
     * @return proxy server bootstrap for chaining
     */
    FurionHttpServerBootstrap withThreadPoolConfiguration(ThreadPoolConfiguration configuration);
}