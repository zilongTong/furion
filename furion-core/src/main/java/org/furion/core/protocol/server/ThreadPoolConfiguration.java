package org.furion.core.protocol.server;


public class ThreadPoolConfiguration {
    private int acceptorThreads = ServerGroup.DEFAULT_INCOMING_ACCEPTOR_THREADS;
    private int clientToGatewayWorkerThreads = ServerGroup.DEFAULT_INCOMING_WORKER_THREADS;
    private int gatewayToServerWorkerThreads = ServerGroup.DEFAULT_OUTGOING_WORKER_THREADS;

    public int getClientToGatewayWorkerThreads() {
        return clientToGatewayWorkerThreads;
    }

    /**
     * Set the number of client-to-Gateway worker threads to create. Worker threads perform the actual processing of
     * client requests. The default value is {@link ServerGroup#DEFAULT_INCOMING_WORKER_THREADS}.
     *
     * @param clientToGatewayWorkerThreads number of client-to-Gateway worker threads to create
     * @return this thread pool configuration instance, for chaining
     */
    public ThreadPoolConfiguration withClientToGatewayWorkerThreads(int clientToGatewayWorkerThreads) {
        this.clientToGatewayWorkerThreads = clientToGatewayWorkerThreads;
        return this;
    }

    public int getAcceptorThreads() {
        return acceptorThreads;
    }

    /**
     * Set the number of acceptor threads to create. Acceptor threads accept HTTP connections from the client and queue
     * them for processing by client-to-Gateway worker threads. The default value is
     * {@link ServerGroup#DEFAULT_INCOMING_ACCEPTOR_THREADS}.
     *
     * @param acceptorThreads number of acceptor threads to create
     * @return this thread pool configuration instance, for chaining
     */
    public ThreadPoolConfiguration withAcceptorThreads(int acceptorThreads) {
        this.acceptorThreads = acceptorThreads;
        return this;
    }

    public int getGatewayToServerWorkerThreads() {
        return gatewayToServerWorkerThreads;
    }

    /**
     * Set the number of gateway-to-server worker threads to create. gateway-to-server worker threads make requests to
     * upstream servers and process responses from the server. The default value is
     * {@link ServerGroup#DEFAULT_OUTGOING_WORKER_THREADS}.
     *
     * @param gatewayToServerWorkerThreads number of gateway-to-server worker threads to create
     * @return this thread pool configuration instance, for chaining
     */
    public ThreadPoolConfiguration withGatewayToServerWorkerThreads(int gatewayToServerWorkerThreads) {
        this.gatewayToServerWorkerThreads = gatewayToServerWorkerThreads;
        return this;
    }

}
