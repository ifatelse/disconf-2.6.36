package com.baidu.disconf.core.common.remote.netty;

import com.baidu.disconf.core.common.utils.GsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Description : 编解码器
 * @Author : Lethe
 * @Date : 2023/3/3 14:00
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byte[] bytes = GsonUtils.toJson(message).getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readInt();

        if(byteBuf.readableBytes() < length) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);

        Message message = GsonUtils.fromJson(new String(bytes), Message.class);

        byteBuf.markReaderIndex();

        list.add(message);
    }


}
