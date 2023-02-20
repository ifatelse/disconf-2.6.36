package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.RequestHandler;
import com.baidu.disconf.core.common.remote.Response;
import com.baidu.disconf.core.common.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/13 16:04
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class MessageHandler {

    @Autowired
    RequestHandlerRegistry requestHandlerRegistry;


    public void handler(ChannelHandlerContext ctx, Object msg) {

        Map<String, Object> objectMap = GsonUtils.toObjectMap((String) msg);

        String msgType = (String) objectMap.get("msgType");

        RequestHandler requestHandler = requestHandlerRegistry.getByRequestType(msgType);

        if (requestHandler == null) {
            return;
        }

        Class<?> requestClass = requestHandlerRegistry.getByRequestClassType(msgType);

        Object requestObj = GsonUtils.fromJson((String) msg, requestClass);

        Response response = requestHandler.handle((Request) requestObj);

        ctx.channel().writeAndFlush(GsonUtils.toJson(response));

    }
}
