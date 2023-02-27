package com.baidu.disconf.core.common.remote;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 15:57
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class RequestHandler<T extends Request, S extends Response> {

    public abstract S handle(T request, ChannelHandlerContext ctx);

}
