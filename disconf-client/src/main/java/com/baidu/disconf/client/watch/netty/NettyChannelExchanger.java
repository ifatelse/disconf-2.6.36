package com.baidu.disconf.client.watch.netty;

import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.core.common.remote.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 17:08
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class NettyChannelExchanger {

    private static final Log log = LogFactory.getLog(NettyClientHandler.class);

    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1,
            new DefaultThreadFactory("client-heartbeat", true));

    private static int heartbeat = 10000;

    private static NettyClient client;

    private static MessageHandler messageHandler;


    public static void connect(String serverIp, int serverPort, MessageHandler messageHandler1, ResponseHandler responseHandler) {
        messageHandler = messageHandler1;
        client = new NettyClient(serverIp, serverPort);
        ResponseHandlerRegistry.registry(responseHandler);
        scheduled.scheduleWithFixedDelay(new HeartBeatTask(), heartbeat, heartbeat, TimeUnit.MILLISECONDS);
    }

    public static void executeConfigListen(String fileName, DisConfCommonModel disConfCommonModel) {

        ConfigChangeRequest configChangeRequest = new ConfigChangeRequest();
        configChangeRequest.setAppName(disConfCommonModel.getApp());
        configChangeRequest.setVersion(disConfCommonModel.getVersion());
        configChangeRequest.setEnv(disConfCommonModel.getEnv());
        configChangeRequest.setFileName(fileName);

        Message message = new Message();
        message.setMsgType(ConfigChangeRequest.class.getSimpleName());
        message.setData(configChangeRequest);

        client.send(message);

    }

    public static void handler(ChannelHandlerContext ctx, Object msg) {
        messageHandler.handler(ctx, msg);
    }

    static class HeartBeatTask implements Runnable {

        @Override
        public void run() {
            HeartBeatRequest heartBeatRequest = new HeartBeatRequest();
            Message message = new Message();
            message.setMsgType(HeartBeatRequest.class.getSimpleName());
            message.setData(heartBeatRequest);
            client.send(message);
        }
    }

}