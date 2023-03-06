package com.baidu.disconf.client.watch.netty;

import com.baidu.disconf.core.common.remote.*;
import com.baidu.disconf.core.common.utils.GsonUtils;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 18:11
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ResponseMessageHandler extends MessageHandler {

    @Override
    public void handler(ChannelHandlerContext ctx, Object msg) {

        Message message = (Message) msg;

        String event = message.getMsgType();

        ResponseHandler responseHandler = ResponseHandlerRegistry.getResponseType(event);

        if (responseHandler == null) {
            return;
        }

        Class<?> requestClass = ResponseHandlerRegistry.getResponseClassType(event);

        Object requestObj = GsonUtils.fromJson(GsonUtils.toJson(message.getData()), requestClass);

        responseHandler.handle((Response) requestObj, ctx.channel());

    }

}
