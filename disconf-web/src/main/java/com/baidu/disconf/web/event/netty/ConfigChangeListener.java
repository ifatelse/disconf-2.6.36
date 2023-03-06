package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.ConfigChangeResponse;
import com.baidu.disconf.core.common.remote.Message;
import com.baidu.disconf.core.common.utils.DisconfThreadFactory;
import com.baidu.disconf.web.event.AbstractEventListener;
import com.baidu.disconf.web.event.ConfigChangeEvent;
import com.baidu.disconf.web.event.Event;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/6 14:34
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigChangeListener extends AbstractEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeListener.class);

    ExecutorService notifyExecutorService = Executors.newSingleThreadExecutor(DisconfThreadFactory.create("ConfigChangeListener", true));


    @Override
    public List<Class<? extends Event>> interest() {
        List<Class<? extends Event>> types = new ArrayList<>();
        types.add(ConfigChangeEvent.class);
        return types;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof ConfigChangeEvent) {
            ConfigChangeEvent evt = (ConfigChangeEvent) event;
            String confName = evt.confName;
            String confKey = evt.confKey;
            logger.info("change confName:{}, confKey:{}", confName, confKey);
            notifyExecutorService.execute(new ConfigChangeTask(confName, confKey));
        }
    }


    class ConfigChangeTask implements Runnable {

        final String confName;
        final String confKey;

        public ConfigChangeTask(String confName, String confKey) {
            this.confName = confName;
            this.confKey = confKey;
        }

        @Override
        public void run() {
            Collection<ChannelHandlerContext> contextList = NettyChannelService.getWatchCtxList(confKey);
            for (ChannelHandlerContext ctx : contextList) {
                ConfigChangeResponse configChangeResponse = new ConfigChangeResponse();
                configChangeResponse.setStatus(Constants.CONFIG_CHANGE);
                configChangeResponse.setFileName(confName);
                Message message = new Message();
                message.setMsgType(ConfigChangeResponse.class.getSimpleName());
                message.setData(configChangeResponse);
                ctx.channel().writeAndFlush(message);
            }
        }
    }
}
