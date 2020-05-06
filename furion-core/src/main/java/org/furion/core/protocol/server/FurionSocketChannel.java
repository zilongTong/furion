package org.furion.core.protocol.server;

import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FurionSocketChannel {
    SocketChannel socketChannel;
    volatile boolean isFree;
}
