package com.baidu.disconf.core.common.remote.netty;

import com.baidu.disconf.core.common.remote.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 15:57
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class RequestHandler<T extends Request> {

    public abstract void handle(T request, ChannelHandlerContext ctx);

}
