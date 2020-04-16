package org.furion.core.protocol.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Timer;
import org.furion.core.bean.eureka.Server;
import org.furion.core.protocol.client.handler.FurionClientHandler;

import java.util.concurrent.TimeUnit;

/**
 * SslHandler:负责对请求进行加密和解密，是放在ChannelPipeline中的第一个ChannelHandler
 * <p>
 * HttpClientCodec和HttpServerCodec:HttpClientCodec负责将请求字节解码为HttpRequest、HttpContent和LastHttpContent消息，以及对应的转为字节；HttpServerCodec负责服务端中将字节码解析成HttpResponse、HttpContent和LastHttpContent消息，以及对应的将它转为字节
 * HttpServerCodec 里面组合了HttpResponseEncoder和HttpRequestDecoder
 * HttpClientCodec 里面组合了HttpRequestEncoder和HttpResponseDecoder
 * <p>
 * HttpObjectAggregator: 负责将http聚合成完整的消息，而不是原始的多个部分
 * HttpContentCompressor和HttpContentDecompressor:HttpContentCompressor用于服务器压缩数据，HttpContentDecompressor用于客户端解压数据
 * IdleStateHandler:连接空闲时间过长，触发IdleStateEvent事件
 * ReadTimeoutHandler:指定时间内没有收到任何的入站数据，抛出ReadTimeoutException异常,并关闭channel
 * WriteTimeoutHandler:指定时间内没有任何出站数据写入，抛出WriteTimeoutException异常，并关闭channel
 * DelimiterBasedFrameDecoder:使用任何用户提供的分隔符来提取帧的通用解码器
 * FixedLengthFrameDecoder:提取在调用构造函数时的定长帧
 * ChunkedWriteHandler：将大型文件从文件系统复制到内存【DefaultFileRegion进行大型文件传输】
 */
public class FurionClientChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Bootstrap bootstrap;

    private final Server server;
    private final Timer timer;//定时器
    private volatile boolean reconnect = true;

    public FurionClientChannelInitializer(Bootstrap bootstrap, Server server, Timer timer, Boolean reConnect) {
        this.bootstrap = bootstrap;
        this.server = server;
        this.timer = timer;
        this.reconnect = reConnect;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast("idleStateHandler", new IdleStateHandler(60000L, 0, 0, TimeUnit.MILLISECONDS));

        //处理http服务的关键handler
        pipeline.addLast("codec", new HttpClientCodec());
//        pipeline.addLast("decoder", new HttpRequestDecoder());

        pipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));

        pipeline.addLast("handler", new FurionClientHandler(bootstrap, timer, server, reconnect));


    }
}
