package com.baidu.disconf.client.watch.inner;


import java.util.HashMap;
import java.util.Map;

/**
 * @Description :
 * @Author : liudd12
 * @Date : 2022/12/5 16:43
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class DisConfConfigService {

    private final Map<String, RemoteConfigRepository> listenerMap = new HashMap<>();

    protected static final DisConfConfigService INSTANCE = new DisConfConfigService();

    public static DisConfConfigService getInstance() {
        return INSTANCE;
    }

    public void addListener(String key, RemoteConfigRepository remoteConfigRepository) {
        listenerMap.put(key, remoteConfigRepository);
    }

    public Map<String, RemoteConfigRepository> listenerMap() {
        return listenerMap;
    }

}
