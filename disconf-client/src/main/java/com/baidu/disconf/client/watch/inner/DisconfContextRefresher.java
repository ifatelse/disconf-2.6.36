package com.baidu.disconf.client.watch.inner;


import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.client.config.DisClientConfig;
import com.baidu.disconf.client.config.DisClientSysConfig;
import com.baidu.disconf.client.core.processor.DisconfCoreProcessor;
import com.baidu.disconf.client.watch.netty.ConfigChangeResponseHandler;
import com.baidu.disconf.client.watch.netty.NettyChannelExchanger;
import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.core.common.path.DisconfWebPathMgr;
import com.baidu.disconf.core.common.utils.DisconfThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/12/6 13:38
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class DisconfContextRefresher implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(DisconfContextRefresher.class);

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), DisconfThreadFactory.create("RemoteConfigLongPollService", true));

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        String type = contextRefreshedEvent.getApplicationContext().getEnvironment().getProperty("listen.type", "http");

        RemoteConfigRepository configRepository = DisConfConfigService.getInstance().getConfigRepository();
        if (configRepository != null) {

            logger.info("listen config change type: {}", type);

            if (Objects.equals(type, Constants.LISTEN_TYPE_HTTP)) {
                executorService.execute(new LongPollingRunnable(DisClientConfig.getInstance().APP, configRepository));
            } else {
                NettyChannelExchanger nettyChannelExchanger = new NettyChannelExchanger("127.0.0.1", Constants.NETTY_PORT, new ConfigChangeResponseHandler());
                nettyChannelExchanger.executeConfigListen(DisClientConfig.getInstance().APP, configRepository.disConfCommonModel);
            }
        }
    }

    class LongPollingRunnable implements Runnable {

        private final String key;

        private final RemoteConfigRepository configRepository;

        public LongPollingRunnable(String key, RemoteConfigRepository configRepository) {
            this.key = key;
            this.configRepository = configRepository;
        }

        @Override
        public void run() {
            try {
                String url = assembleLongPollRefreshUrl(key, configRepository.disConfCommonModel);
                ValueVo valueVo = configRepository.fetcherMgr.getChangeFileFromServer(url);
                if (Objects.equals(valueVo.getStatus(), Constants.CONFIG_CHANGE)) {
                    String key = valueVo.getValue();
                    logger.info("config change:{}", key);
                    DisconfCoreProcessor leconfCoreProcessor = DisConfConfigService.INSTANCE.getCoreProcessor(key);
                    leconfCoreProcessor.updateOneConfAndCallback(key);
                }
                executorService.execute(this);
            } catch (Exception e) {
                logger.error("longPolling error : ", e);
                executorService.schedule(this, 3000, TimeUnit.MILLISECONDS);
            }
        }
    }

    private String assembleLongPollRefreshUrl(String key, DisConfCommonModel disConfCommonModel) {
        String app = disConfCommonModel.getApp();
        String version = disConfCommonModel.getVersion();
        String env = disConfCommonModel.getEnv();
        return DisconfWebPathMgr.getRemoteUrlParameter(DisClientSysConfig.getInstance().CONF_SERVER_NOTIFY_ACTION, app, version, env, key);
    }


}
