package com.baidu.disconf.core.common.remote;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 18:04
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class MessageHandler {

    public abstract void handler(ChannelHandlerContext ctx, Object msg);

}
