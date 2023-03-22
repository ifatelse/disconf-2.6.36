package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.remote.grpc.GrpcRequestHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/13 13:37
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class GrpcRequestHandlerRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private final Map<String, GrpcRequestHandler> registryHandlers = new HashMap<>();
    private final Map<String, Class<?>> REGISTRY_REQUEST = new HashMap<>();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        Map<String, GrpcRequestHandler> beansOfType = event.getApplicationContext().getBeansOfType(GrpcRequestHandler.class);
        Collection<GrpcRequestHandler> values = beansOfType.values();
        for (GrpcRequestHandler requestHandler : values) {

            Class<?> clazz = requestHandler.getClass();
            boolean skip = false;
            while (!clazz.getSuperclass().equals(GrpcRequestHandler.class)) {
                if (clazz.getSuperclass().equals(Object.class)) {
                    skip = true;
                    break;
                }
                clazz = clazz.getSuperclass();
            }
            if (skip) {
                continue;
            }

            Class<?> tClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            registryHandlers.putIfAbsent(tClass.getSimpleName(), requestHandler);
            REGISTRY_REQUEST.put(tClass.getSimpleName(), tClass);
        }
    }

    public GrpcRequestHandler getByRequestType(String requestType) {
        return registryHandlers.get(requestType);
    }

    public Class<?> getByRequestClassType(String requestType) {
        return REGISTRY_REQUEST.get(requestType);
    }
}
