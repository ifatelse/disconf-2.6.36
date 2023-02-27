package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.remote.ConfigChangeResponse;
import com.baidu.disconf.core.common.utils.GsonUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelService {

    private static Map<String, ChannelHandlerContext> clientChannelMap = new ConcurrentHashMap<>();

    private static Multimap<String, String> contextSetMultimap =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

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

    public static void listenerChannel(String key, ChannelHandlerContext ctx) {
        String address = toAddressString((InetSocketAddress) ctx.channel().remoteAddress());
        contextSetMultimap.put(key, address);
    }

    public static void notifyChange(String key, String fileName) {

        Collection<String> addressList = contextSetMultimap.get(key);
        for (String address : addressList) {

            ChannelHandlerContext ctx = getChannel(address);
            if (ctx == null) {
                // 删除键key下面的某个值
                contextSetMultimap.remove(key, address);
                // 删除所有健下的某个值
                // contextSetMultimap.values().remove(address);
                return;
            }

            ConfigChangeResponse configChangeResponse = new ConfigChangeResponse();
            configChangeResponse.setFileName(fileName);
            configChangeResponse.setMsgType(ConfigChangeResponse.class.getSimpleName());

            ByteBuf data = Unpooled.wrappedBuffer(GsonUtils.toJson(configChangeResponse).getBytes(CharsetUtil.UTF_8));
            int length = data.readableBytes();
            ByteBuf buffer = Unpooled.buffer(2 + length);
            buffer.writeShort(length);
            buffer.writeBytes(data);

            ctx.channel().writeAndFlush(buffer);

        }

    }


    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}