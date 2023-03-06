package com.baidu.disconf.web.event.netty;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelService {

    private static final Map<String, ChannelHandlerContext> clientChannelMap = new ConcurrentHashMap<>();

    private static final Map<String, String> addressWatchkeyMap = new ConcurrentHashMap<>();

    private static final Multimap<String, ChannelHandlerContext> contextSetMultimap =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());


    public static void saveChannel(String address, ChannelHandlerContext ctx) {
        clientChannelMap.put(address, ctx);
    }


    public static void removeChannel(String address) {
        ChannelHandlerContext ctx = clientChannelMap.remove(address);
        String watchKey = addressWatchkeyMap.remove(address);
        contextSetMultimap.remove(watchKey, ctx);
    }

    public static void listenerChannel(String watchKey, ChannelHandlerContext ctx) {
        String address = toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
        addressWatchkeyMap.put(address, watchKey);
        contextSetMultimap.put(watchKey, ctx);
    }


    public static Collection<ChannelHandlerContext> getWatchCtxList(String watchKey) {
        return contextSetMultimap.get(watchKey);
    }

    public static boolean watchCtxIsEmpty() {
        return contextSetMultimap.isEmpty();
    }


    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}