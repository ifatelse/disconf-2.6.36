package com.baidu.disconf.client.watch.netty;

import com.baidu.disconf.core.common.remote.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 17:06
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class NettyClient {

    private static final Log log = LogFactory.getLog(NettyClient.class);

    private final String serverIp;

    private final int serverPort;

    private Channel channel;

    public NettyClient(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        start();
    }

    private void start() {

        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        final NettyClientHandler nettyClientHandler = new NettyClientHandler();

        bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast("codec", new MessageCodec())
                                .addLast("client-idle-handler", new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS))
                                .addLast(nettyClientHandler);
                    }
                });

        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(serverIp, serverPort));

        boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
        if (ret && future.isSuccess()) {
            channel = future.channel();
        } else if (future.cause() != null) {
            throw new RuntimeException("failed to connect to server " + serverIp + ":" + serverPort + ", error message is:" + future.cause().getMessage(), future.cause());
        } else {
            throw new RuntimeException("failed to connect to server " + serverIp + ":" + serverPort + ", client-side timeout "
                    + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms)");
        }
    }

    public void send(Object msg) {
        channel.writeAndFlush(msg);
    }

    protected int getConnectTimeout() {
        return 10000;
    }
}
