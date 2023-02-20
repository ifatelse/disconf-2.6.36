package com.baidu.disconf.web.event.netty;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelService {

    private static Map<String, ChannelHandlerContext> clientChannelMap = new ConcurrentHashMap<>();

    public static Map<String, ChannelHandlerContext> getChannels() {
        return clientChannelMap;
    }

    public static void saveChannel(String key, ChannelHandlerContext ctx) {
        if (clientChannelMap == null) {
            clientChannelMap = new ConcurrentHashMap<>();
        }
        clientChannelMap.put(key, ctx);
    }

    public static ChannelHandlerContext getChannel(String key) {
        if (clientChannelMap == null || clientChannelMap.isEmpty()) {
            return null;
        }
        return clientChannelMap.get(key);
    }

    public static void removeChannel(String key) {
        clientChannelMap.remove(key);
    }
}