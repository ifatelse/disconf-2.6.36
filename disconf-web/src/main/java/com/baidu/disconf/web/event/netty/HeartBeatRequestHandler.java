package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.HeartBeatRequest;
import com.baidu.disconf.core.common.remote.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 15:42
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class HeartBeatRequestHandler extends RequestHandler<HeartBeatRequest> {


    @Override
    public void handle(HeartBeatRequest request, ChannelHandlerContext ctx) {

    }
}
