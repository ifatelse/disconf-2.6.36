package com.baidu.disconf.core.common.remote;

import io.netty.channel.Channel;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 17:41
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class ResponseHandler<T extends Response> {

    public abstract void handle(T response, Channel channel);

}
