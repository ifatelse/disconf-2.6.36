package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.RequestHandler;
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
 * @Date : 2023/2/14 16:04
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class RequestHandlerRegistry implements ApplicationListener<ContextRefreshedEvent> {

    private static final Map<String, RequestHandler<?>> REGISTRY_HANDLERS = new HashMap<>();

    private static final Map<String, Class<?>> REGISTRY_REQUEST = new HashMap<>();


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Map<String, RequestHandler> beansOfType = event.getApplicationContext().getBeansOfType(RequestHandler.class);
        Collection<RequestHandler> values = beansOfType.values();
        for (RequestHandler<?> requestHandler : values) {

            Class<?> clazz = requestHandler.getClass();
            boolean skip = false;
            while (!clazz.getSuperclass().equals(RequestHandler.class)) {
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

            REGISTRY_HANDLERS.putIfAbsent(tClass.getSimpleName(), requestHandler);

            REGISTRY_REQUEST.put(tClass.getSimpleName(), tClass);

        }
    }


    public RequestHandler<?> getByRequestType(String requestType) {
        return REGISTRY_HANDLERS.get(requestType);
    }

    public Class<?> getByRequestClassType(String requestType) {
        return REGISTRY_REQUEST.get(requestType);
    }
}
