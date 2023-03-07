package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.Message;
import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.RequestHandler;
import com.baidu.disconf.core.common.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/13 16:04
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class RequestMessageHandler {

    @Autowired
    RequestHandlerRegistry requestHandlerRegistry;


    public void handler(ChannelHandlerContext ctx, Object msg) {


        Message message = (Message) msg;

        String event = message.getMsgType();

        RequestHandler requestHandler = requestHandlerRegistry.getByRequestType(event);

        if (requestHandler == null) {
            return;
        }

        Class<?> requestClass = requestHandlerRegistry.getByRequestClassType(event);

        Object requestObj = GsonUtils.fromJson(GsonUtils.toJson(message.getData()), requestClass);

        requestHandler.handle((Request) requestObj, ctx);

    }

}
