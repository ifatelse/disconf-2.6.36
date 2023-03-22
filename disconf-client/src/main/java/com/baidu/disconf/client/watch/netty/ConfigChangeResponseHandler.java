package com.baidu.disconf.client.watch.netty;


import com.baidu.disconf.client.core.processor.DisconfCoreProcessor;
import com.baidu.disconf.client.watch.inner.DisConfConfigService;
import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.ConfigChangeResponse;
import com.baidu.disconf.core.common.remote.netty.ResponseHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @Description :
 * @Author : liudd12@lenovo.com
 * @Date : 2023/3/3 18:24
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigChangeResponseHandler extends ResponseHandler<ConfigChangeResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeResponseHandler.class);

    @Override
    public void handle(ConfigChangeResponse response, Channel channel) {
        if (Objects.equals(response.getStatus(), Constants.CONFIG_CHANGE)) {
            String key = response.getFileName();
            logger.info("config change:{}", key);
            try {
                DisconfCoreProcessor disconfCoreProcessor = DisConfConfigService.getInstance().getCoreProcessor(key);
                disconfCoreProcessor.updateOneConfAndCallback(key);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
