package com.baidu.disconf.web.event.netty;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.remote.ConfigQueryRequest;
import com.baidu.disconf.core.common.remote.ConfigQueryResponse;
import com.baidu.disconf.core.common.remote.RequestHandler;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.service.config.service.ConfigFetchMgr;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 15:58
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class ConfigQueryRequestHandler extends RequestHandler<ConfigQueryRequest, ConfigQueryResponse> {

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Autowired
    private ConfigFetchMgr configFetchMgr;


    @Override
    public ConfigQueryResponse handle(ConfigQueryRequest request, ChannelHandlerContext ctx) {

        ConfForm confForm = new ConfForm();
        confForm.setApp(request.getAppName());
        confForm.setEnv(request.getEnv());
        confForm.setKey(request.getFileName());
        confForm.setVersion(request.getVersion());

        ConfigFullModel configModel = configValidator4Fetch.verifyConfForm(confForm, false);

        Config config = configFetchMgr.getConfByParameter(configModel.getApp().getId(), configModel.getEnv().getId(),
                configModel.getVersion(), configModel.getKey(), DisConfigTypeEnum.FILE);

        String value = config.getValue();

        ConfigQueryResponse configQueryResponse = new ConfigQueryResponse();
        configQueryResponse.setRequestId(request.getRequestId());
        configQueryResponse.setMsgType(ConfigQueryResponse.class.getSimpleName());
        configQueryResponse.setFileName(confForm.getKey());
        configQueryResponse.setValue(value);

        return configQueryResponse;
    }


}
