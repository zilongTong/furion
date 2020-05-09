package org.furion.core.protocol.server;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class FurionSocketChannel {

    private final SocketChannel socketChannel;

    private volatile boolean isFree;

    private transient Long exclusiveOwnerRequest;

    public FurionSocketChannel(SocketChannel socketChannel, boolean isFree) {
        this.socketChannel = socketChannel;
        this.isFree = isFree;
    }

    public FurionSocketChannel(SocketChannel socketChannel, boolean isFree, Long requestId) {
        this.socketChannel = socketChannel;
        this.isFree = isFree;
        this.exclusiveOwnerRequest = requestId;
    }
}
