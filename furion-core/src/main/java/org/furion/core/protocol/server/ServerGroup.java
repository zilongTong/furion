package org.furion.core.protocol.server;

import io.netty.channel.EventLoopGroup;

import org.furion.core.enumeration.ProtocolType;
import org.furion.core.exception.UnknownTransportProtocolException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class ServerGroup {
    private static final Logger log = LoggerFactory.getLogger(ServerGroup.class);


    public static final int DEFAULT_INCOMING_ACCEPTOR_THREADS = 2;


    public static final int DEFAULT_INCOMING_WORKER_THREADS = 8;


    public static final int DEFAULT_OUTGOING_WORKER_THREADS = 8;


    private static final AtomicInteger serverGroupCount = new AtomicInteger(0);


    private final String name;


    private final int serverGroupId;

    private final int incomingAcceptorThreads;
    private final int incomingWorkerThreads;
    private final int outgoingWorkerThreads;

    /**
     * List of all servers registered to use this ServerGroup. Any access to this list should be synchronized using the
     * {@link #SERVER_REGISTRATION_LOCK}.
     */
    public final List<FurionServerNetWork> registeredServers = new ArrayList<>(1);


    private final EnumMap<ProtocolType, FurionThreadPools> protocolThreadPools = new EnumMap<>(ProtocolType.class);

    /**
     * A mapping of selector providers to transport protocols. Avoids special-casing each transport protocol during
     * transport protocol initialization.
     */
    private static final EnumMap<ProtocolType, SelectorProvider> TRANSPORT_PROTOCOL_SELECTOR_PROVIDERS = new EnumMap<ProtocolType, SelectorProvider>(ProtocolType.class);

    static {
        TRANSPORT_PROTOCOL_SELECTOR_PROVIDERS.put(ProtocolType.TCP, SelectorProvider.provider());
        // allow the proxy to operate without UDT support. this allows clients that do not use UDT to exclude the barchart
        // dependency completely.
//        if (NioUdtProvider.BYTE_PROVIDER != null;) {
//            TRANSPORT_PROTOCOL_SELECTOR_PROVIDERS.put(ProtocolType.UDT, NioUdtProvider.BYTE_PROVIDER);
//        } else {
//            log.debug("UDT provider not found on classpath. UDT transport will not be available.");
//        }
    }

    /**
     * True when this ServerGroup is stopped.
     */
    private final AtomicBoolean stopped = new AtomicBoolean(false);


    public ServerGroup(String name, int incomingAcceptorThreads, int incomingWorkerThreads, int outgoingWorkerThreads) {
        this.name = name;
        this.serverGroupId = serverGroupCount.getAndIncrement();
        this.incomingAcceptorThreads = incomingAcceptorThreads;
        this.incomingWorkerThreads = incomingWorkerThreads;
        this.outgoingWorkerThreads = outgoingWorkerThreads;
    }

    /**
     * Lock for initializing any transport protocols.
     */
    private final Object THREAD_POOL_INIT_LOCK = new Object();


    private FurionThreadPools getThreadPoolsForProtocol(ProtocolType protocol) {
        // if the thread pools have not been initialized for this protocol, initialize them
        if (protocolThreadPools.get(protocol) == null) {
            synchronized (THREAD_POOL_INIT_LOCK) {
                if (protocolThreadPools.get(protocol) == null) {
                    log.debug("Initializing thread pools for {} with {} acceptor threads, {} incoming worker threads, and {} outgoing worker threads",
                            protocol, incomingAcceptorThreads, incomingWorkerThreads, outgoingWorkerThreads);

                    SelectorProvider selectorProvider = TRANSPORT_PROTOCOL_SELECTOR_PROVIDERS.get(protocol);
                    if (selectorProvider == null) {
                        throw new UnknownTransportProtocolException(protocol);
                    }

                    FurionThreadPools threadPools = new FurionThreadPools(selectorProvider,
                            incomingAcceptorThreads,
                            incomingWorkerThreads,
                            outgoingWorkerThreads,
                            name,
                            serverGroupId);
                    protocolThreadPools.put(protocol, threadPools);
                }
            }
        }

        return protocolThreadPools.get(protocol);
    }


    private final Object SERVER_REGISTRATION_LOCK = new Object();


    public void registerProxyServer(FurionServerNetWork netWork) {
        synchronized (SERVER_REGISTRATION_LOCK) {
            registeredServers.add(netWork);
        }
    }


    public void unregisterProxyServer(FurionServerNetWork proxyServer, boolean graceful) {
        synchronized (SERVER_REGISTRATION_LOCK) {
            boolean wasRegistered = registeredServers.remove(proxyServer);
            if (!wasRegistered) {
                log.warn("Attempted to unregister proxy server from ServerGroup that it was not registered with. Was the proxy unregistered twice?");
            }

            if (registeredServers.isEmpty()) {
                log.debug("Proxy server unregistered from ServerGroup. No proxy servers remain registered, so shutting down ServerGroup.");

                shutdown(graceful);
            } else {
                log.debug("Proxy server unregistered from ServerGroup. Not shutting down ServerGroup ({} proxy servers remain registered).", registeredServers.size());
            }
        }
    }

    /**
     * Shuts down all event loops owned by this server group.
     *
     * @param graceful when true, event loops will "gracefully" terminate, waiting for submitted tasks to finish
     */
    private void shutdown(boolean graceful) {
        if (!stopped.compareAndSet(false, true)) {
            log.info("Shutdown requested, but ServerGroup is already stopped. Doing nothing.");

            return;
        }

        log.info("Shutting down server group event loops " + (graceful ? "(graceful)" : "(non-graceful)"));

        // loop through all event loops managed by this server group. this includes acceptor and worker event loops
        // for both TCP and UDP transport protocols.
        List<EventLoopGroup> allEventLoopGroups = new ArrayList<EventLoopGroup>();

        for (FurionThreadPools threadPools : protocolThreadPools.values()) {
            allEventLoopGroups.addAll(threadPools.getAllEventLoops());
        }

        for (EventLoopGroup group : allEventLoopGroups) {
            if (graceful) {
                group.shutdownGracefully();
            } else {
                group.shutdownGracefully(0, 0, TimeUnit.SECONDS);
            }
        }

        if (graceful) {
            for (EventLoopGroup group : allEventLoopGroups) {
                try {
                    group.awaitTermination(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    log.warn("Interrupted while shutting down event loop");
                }
            }
        }

        log.debug("Done shutting down server group");
    }


    public EventLoopGroup getClientToGatewayAcceptorPoolForTransport(ProtocolType protocol) {
        return getThreadPoolsForProtocol(protocol).getClientToGatewayAcceptorPool();
    }


    public EventLoopGroup getClientToGatewayWorkerPoolForTransport(ProtocolType protocol) {
        return getThreadPoolsForProtocol(protocol).getClientToGatewayWorkerPool();
    }


    public EventLoopGroup getGatewayToServerWorkerPoolForTransport(ProtocolType protocol) {
        return getThreadPoolsForProtocol(protocol).getGatewayToServerWorkerPool();
    }

    /**
     * @return true if this ServerGroup has already been stopped
     */
    public boolean isStopped() {
        return stopped.get();
    }

}
