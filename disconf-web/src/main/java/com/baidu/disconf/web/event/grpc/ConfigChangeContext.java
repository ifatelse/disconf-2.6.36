package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.grpc.auto.Message;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.grpc.stub.StreamObserver;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/14 14:27
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigChangeContext {


    private static final Multimap<String, String> watchKeySetMultimap = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    private static final Map<String, StreamObserver<Message>> observerMap = new ConcurrentHashMap<>();
    private static final Map<String, String> connectionIdMap = new ConcurrentHashMap<>();

    public static void listenKey(String watchKey, String connectionId) {
        watchKeySetMultimap.put(watchKey, connectionId);
        connectionIdMap.put(connectionId, watchKey);
    }

    public static void listenStream(String connectionId, StreamObserver<Message> streamObserver) {
        observerMap.put(connectionId, streamObserver);
    }

    public static void removeListen(String connectionId) {
        String watchKey = connectionIdMap.remove(connectionId);
        observerMap.remove(connectionId);
        watchKeySetMultimap.remove(watchKey, connectionId);
    }

    public static Collection<String> getWatchCtxList(String watchKey) {
        return watchKeySetMultimap.get(watchKey);
    }

    public static StreamObserver<Message> getStreamObserver(String connectionId) {
        return observerMap.get(connectionId);
    }

    public static boolean observerIsEmpty() {
        return observerMap.isEmpty();
    }

}
