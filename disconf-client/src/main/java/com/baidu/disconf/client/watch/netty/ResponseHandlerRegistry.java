package com.baidu.disconf.client.watch.netty;

import com.baidu.disconf.core.common.remote.ResponseHandler;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 18:13
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ResponseHandlerRegistry {

    private static final Map<String, ResponseHandler<?>> RESPONSE_HANDLERS = new HashMap<>();

    private static final Map<String, Class<?>> REGISTRY_RESPONSE = new HashMap<>();


    public static void registry(ResponseHandler responseHandler) {
        responseHandler(responseHandler);
    }

    private static void responseHandler(ResponseHandler responseHandler) {

        Class<? extends ResponseHandler> clazz = responseHandler.getClass();

        if (clazz.getSuperclass().equals(ResponseHandler.class)) {

            Class<?> tClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];

            RESPONSE_HANDLERS.putIfAbsent(tClass.getSimpleName(), responseHandler);

            REGISTRY_RESPONSE.put(tClass.getSimpleName(), tClass);

        }

    }

    public static ResponseHandler<?> getResponseType(String requestType) {
        return RESPONSE_HANDLERS.get(requestType);
    }

    public static Class<?> getResponseClassType(String requestType) {
        return REGISTRY_RESPONSE.get(requestType);
    }
}
