package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.ConfigChangeRequest;
import com.baidu.disconf.core.common.remote.ConfigChangeResponse;
import com.baidu.disconf.core.common.remote.RequestHandler;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/23 10:07
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class ConfigChangeRequestHandler extends RequestHandler<ConfigChangeRequest, ConfigChangeResponse> {

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Override
    public ConfigChangeResponse handle(ConfigChangeRequest request, ChannelHandlerContext ctx) {

        ConfForm confForm = new ConfForm();
        confForm.setApp(request.getAppName());
        confForm.setEnv(request.getEnv());
        confForm.setKey(request.getFileName());
        confForm.setVersion(request.getVersion());

        ConfigFullModel configModel = configValidator4Fetch.verifyConfForm(confForm, false);

        String watchKey = configModel.getApp().getId() + Constants.CON_STRING + configModel.getVersion() + Constants.CON_STRING + configModel.getEnv().getId();

        NettyChannelService.listenerChannel(watchKey, ctx);

        ConfigChangeResponse configChangeResponse = new ConfigChangeResponse();
        configChangeResponse.setMsgType(ConfigChangeResponse.class.getSimpleName());
        return configChangeResponse;
    }

}
