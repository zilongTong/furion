package org.furion.core.protocol.server;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.furion.core.enumeration.MsgType;

@Data
public class FurionSocketChannel {

    private final SocketChannel socketChannel;

    private volatile boolean isFree;

    private transient Long exclusiveOwnerRequest;

    private transient MsgType msgType;


    public void releaseChannel() {
        isFree = true;
        exclusiveOwnerRequest = null;
        msgType = null;
    }

    public FurionSocketChannel(SocketChannel socketChannel, boolean isFree) {
        this.socketChannel = socketChannel;
        this.isFree = isFree;
    }

    public FurionSocketChannel(SocketChannel socketChannel, boolean isFree, Long requestId) {
        this.socketChannel = socketChannel;
        this.isFree = isFree;
        this.exclusiveOwnerRequest = requestId;
    }

    public FurionSocketChannel(SocketChannel socketChannel, boolean isFree, Long exclusiveOwnerRequest, MsgType msgType) {
        this.socketChannel = socketChannel;
        this.isFree = isFree;
        this.exclusiveOwnerRequest = exclusiveOwnerRequest;
        this.msgType = msgType;
    }
}
