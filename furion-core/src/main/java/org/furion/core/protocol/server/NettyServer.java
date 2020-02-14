package org.furion.core.protocol.server;


import io.netty.channel.ChannelFuture;
import org.furion.core.protocol.server.handler.GatewayHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyServer {
    ServerBootstrap serverBootstrap;
    NioEventLoopGroup bossGroup;// = new NioEventLoopGroup(1);
    NioEventLoopGroup workerGroup;
    Bootstrap bootstrap;
    EventExecutorGroup executorService = new DefaultEventExecutorGroup(16);

    public NettyServer() {
        bootstrap = new Bootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class)
                .group(bossGroup, workerGroup)
                .option(ChannelOption.SO_BACKLOG, 1024)//服务端请求等待队列
//                .option(ChannelOption.SO_REUSEADDR, true)//重复使用或共用监听端口
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(executorService, new HttpServerCodec(), new HttpObjectAggregator(512 * 1024), new GatewayHandler());
                    }
                });
    }

    public void start() {
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stop();
        }

    }

    public void stop() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        new NettyServer().start();
    }
}
