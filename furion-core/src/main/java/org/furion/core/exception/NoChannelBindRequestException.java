package org.furion.core.exception;

import io.netty.channel.socket.SocketChannel;

public class NoChannelBindRequestException extends RuntimeException {


    public NoChannelBindRequestException(SocketChannel var) {
        super(String.format("No Channel Bind Request Exception : %1$s", var));
    }
}
