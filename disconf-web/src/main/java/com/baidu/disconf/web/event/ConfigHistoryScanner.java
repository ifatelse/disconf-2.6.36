package com.baidu.disconf.web.event;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.service.ConfigHistoryMgr;
import com.baidu.disconf.web.service.config.vo.ConfigHistoryReleaseVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/30 17:47
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
@Component
public class ConfigHistoryScanner implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ConfigHistoryScanner.class);

    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private long maxIdScanned;


    @Autowired
    private ConfigHistoryMgr configHistoryMgr;

    @Override
    public void afterPropertiesSet() throws Exception {
        maxIdScanned = loadLargestHistoryId();
        executorService.scheduleWithFixedDelay((Runnable) () -> {
            // logger.info("execute scanAndSendMessages");
            scanConfigHistoryMessages();
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void scanConfigHistoryMessages() {
        List<ConfigHistoryReleaseVo> historyReleaseList = configHistoryMgr.findFirst500ByIdGreaterThanOrderByIdAsc(maxIdScanned);

        if (CollectionUtils.isEmpty(historyReleaseList)) {
            return;
        }

        fireMessageScanned(historyReleaseList);

        int messageScanned = historyReleaseList.size();
        maxIdScanned = historyReleaseList.get(messageScanned - 1).getId();
    }

    private long loadLargestHistoryId() {
        ConfigHistory configHistory = configHistoryMgr.findTopByOrderByIdDesc();
        return configHistory == null ? 0 : configHistory.getId();
    }

    private void fireMessageScanned(List<ConfigHistoryReleaseVo> historyReleaseList) {
        for (ConfigHistoryReleaseVo configHistoryRelease : historyReleaseList) {
            // todo 配置变更通知
            EventDispatcher.fireEvent(new ConfigChangeEvent(configHistoryRelease.getConfName(), configHistoryRelease.getConfKey()));
        }
    }


}
