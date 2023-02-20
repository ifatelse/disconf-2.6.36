package com.baidu.disconf.client.watch.inner;


import com.baidu.disconf.client.core.processor.DisconfCoreProcessor;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/12/5 16:43
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class DisConfConfigManager {

    private final Map<String, RemoteConfigRepository> listenerMap = new HashMap<>();

    private final Map<String, DisconfCoreProcessor> processorMap = new HashMap<>();


    protected static final DisConfConfigManager INSTANCE = new DisConfConfigManager();

    public static DisConfConfigManager getInstance() {
        return INSTANCE;
    }

    public void addListener(String key, DisconfCoreProcessor disconfCoreProcessor, RemoteConfigRepository remoteConfigRepository) {
        listenerMap.put(key, remoteConfigRepository);
        processorMap.put(key, disconfCoreProcessor);
    }

    public DisconfCoreProcessor getCoreProcessor(String key) {
        return processorMap.get(key);
    }

    public RemoteConfigRepository getContextRepository() {
        if (CollectionUtils.isEmpty(listenerMap)) {
            return null;
        }
        Object[] values = listenerMap.values().toArray();
        Random random = new Random();
        int index = random.nextInt(values.length);
        return (RemoteConfigRepository) values[index];
    }

}
