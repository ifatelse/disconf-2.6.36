package com.baidu.disconf.core.common.utils;

import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.grpc.auto.Message;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/13 13:47
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class GrpcUtils {

    public static Message convert(Object object) {

        String type = object.getClass().getSimpleName();
        String jsonString = GsonUtils.toJson(object);

        return Message.newBuilder()
                .setType(type)
                .setBody(Any.newBuilder().setValue(ByteString.copyFrom(jsonString, StandardCharsets.UTF_8)))
                .build();

    }

    public static Object parse(Message message, Class<?> requestType) {
        return GsonUtils.fromJson(message.getBody().getValue().toString(StandardCharsets.UTF_8), requestType);
    }


}
