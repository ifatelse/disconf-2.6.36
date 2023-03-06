package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.MessageCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/13 13:59
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class NettyServer {

    @Autowired
    private RequestMessageHandler requestMessageHandler;

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    @PostConstruct
    public void start() {

        ServerBootstrap bootstrap = new ServerBootstrap();

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        final NettyServerHandler nettyServerHandler = new NettyServerHandler(requestMessageHandler);


        bootstrap.group(bossGroup, workerGroup)
                // 设置线程队列得到连接个数, 如果队列已满，客户端连接将被拒绝
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                // 当SO_KEEPALIVE=true的时候,服务端可以探测客户端的连接是否还存活着,如果客户端关闭了,那么服务端的连接可以关闭掉,释放资源
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 允许重复使用本地地址和端口
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                // .addLast("lengthFieldBasedFrameDecoder", new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 2))
                                // .addLast("decoder", new StringDecoder())
                                // .addLast("encoder", new StringEncoder())
                                .addLast("codec", new MessageCodec())
                                // readerIdleTime参数指定超过30秒还没收到客户端的连接，会触发IdleStateEvent事件
                                .addLast("server-idle-handler", new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                .addLast(nettyServerHandler);
                    }
                });

        bootstrap.bind(Constants.NETTY_PORT);

        logger.info("Netty Server Start...");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });

    }
}