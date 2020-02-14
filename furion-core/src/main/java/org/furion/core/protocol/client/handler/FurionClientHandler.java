package org.furion.core.protocol.client.handler;

import org.furion.core.protocol.client.lru.CountDownLatchLRUMap;
import org.furion.core.protocol.client.lru.ResponseLRUMap;
import org.furion.core.utils.FurionResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.CountDownLatch;

/**
 * Functional description
 *
 * @author Leo
 * @date 2020-01-02
 */
public class FurionClientHandler extends SimpleChannelInboundHandler<FurionResponse> implements TimerTask {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void run(Timeout timeout) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FurionResponse furionResponse) throws Exception {
        ResponseLRUMap.add(furionResponse.getRequestId(), furionResponse);
        CountDownLatchLRUMap.get(furionResponse.getRequestId()).countDown();
    }
}