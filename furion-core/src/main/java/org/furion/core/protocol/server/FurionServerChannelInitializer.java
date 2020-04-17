package org.furion.core.protocol.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import org.furion.core.protocol.server.handler.FurionServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FurionServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(FurionServerChannelInitializer.class);

    private FurionServerNetWork httpServer;

    public FurionServerChannelInitializer(FurionServerNetWork httpServer) {
        this.httpServer = httpServer;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        System.out.println("initChannel----------------------------------");
        ChannelPipeline pipeline = socketChannel.pipeline();
        initChannelPipeline(pipeline);

//        if (HttpDemoServer.isSSL) {
//            SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
//            engine.setNeedClientAuth(true); //ssl双向认证
//            engine.setUseClientMode(false);
//            engine.setWantClientAuth(true);
//            engine.setEnabledProtocols(new String[]{"SSLv3"});
//            pipeline.addLast("ssl", new SslHandler(engine));
//        }


//        pipeline.addLast("codec", new HttpServerCodec());
//        /**
//         * http-response解码器
//         * http服务器端对response编码
//         */
////        pipeline.addLast("encoder", new HttpResponseEncoder());
//
//        /**
//         * chunk
//         */
//        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
//        /**
//         * 压缩
//         * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
//         * while respecting the "Accept-Encoding" header.
//         * If there is no matching encoding, no compression is done.
//         */
//        pipeline.addLast("deflater", new HttpContentCompressor());
//
//        for (ChannelInboundHandler handler : handlers) {
//            pipeline.addLast("handler", handler);
//        }
    }

    @Sharable
    protected abstract class BytesReadMonitor extends
            ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            try {
                if (msg instanceof ByteBuf) {
                    bytesRead(((ByteBuf) msg).readableBytes());
                }
            } catch (Throwable t) {
                LOG.warn("Unable to record bytesRead", t);
            } finally {
                super.channelRead(ctx, msg);
            }
        }

        protected abstract void bytesRead(int numberOfBytes);
    }

//    private final BytesReadMonitor bytesReadMonitor = new BytesReadMonitor() {
//        @Override
//        protected void bytesRead(int numberOfBytes) {
//            FlowContext flowContext = flowContext();
//            for (ActivityTracker tracker : proxyServer
//                    .getActivityTrackers()) {
//                tracker.bytesReceivedFromClient(flowContext, numberOfBytes);
//            }
//        }
//    };

    private void initChannelPipeline(ChannelPipeline pipeline) {
        LOG.debug("Configuring ChannelPipeline");

//        pipeline.addLast("bytesReadMonitor", bytesReadMonitor);
//        pipeline.addLast("bytesWrittenMonitor", bytesWrittenMonitor);
        pipeline.addLast(
                "idle",
                new IdleStateHandler(0, 0, httpServer
                        .getIdleConnectionTimeout()));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        // We want to allow longer request lines, headers, and chunks
        // respectively.
        pipeline.addLast("decoder", new HttpRequestDecoder(
                httpServer.getMaxInitialLineLength(),
                httpServer.getMaxHeaderSize(),
                httpServer.getMaxChunkSize()));

        // Enable aggregation for filtering if necessary
//        int numberOfBytesToBuffer = proxyServer.getFiltersSource()
//                .getMaximumRequestBufferSizeInBytes();
//        if (numberOfBytesToBuffer > 0) {
//            aggregateContentForFiltering(pipeline, numberOfBytesToBuffer);
//        }

//        pipeline.addLast("requestReadMonitor", requestReadMonitor);
//        pipeline.addLast("responseWrittenMonitor", responseWrittenMonitor);
        pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("deflater", new HttpContentCompressor());

        pipeline.addLast("handler", new FurionServerHandler(httpServer));

    }

}
