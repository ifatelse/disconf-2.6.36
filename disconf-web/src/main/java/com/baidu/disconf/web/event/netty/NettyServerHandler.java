package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.ConfigQueryRequest;
import com.baidu.disconf.core.common.utils.GsonUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/13 14:39
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private MessageHandler messageHandler;

    public NettyServerHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    // 建立连接时，该方法被调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String address = toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
        logger.info("与客户端：{}，建立连接", address);
        NettyChannelService.saveChannel(address, ctx);
    }

    // 连接断开时，此方法被调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String address = toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
        logger.info("与客户端：{}，断开连接", address);
        NettyChannelService.removeChannel(address);
    }

    // 收到消息时，改方法被调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("接收到消息:" + msg);
        messageHandler.handler(ctx, msg);
    }

    // 发送消息
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("发送消息");
        super.write(ctx, msg, promise);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        for (Map.Entry<String, ChannelHandlerContext> contextEntry : NettyChannelService.getChannels().entrySet()) {
            String key = contextEntry.getKey();
            ChannelHandlerContext value = contextEntry.getValue();
            logger.info(key + "------" + value + "-------" + value.channel().isActive());
        }

        if (evt instanceof IdleStateEvent) {

            logger.info("已经5秒未收到客户端的消息了！" + new Date());

        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }
}
