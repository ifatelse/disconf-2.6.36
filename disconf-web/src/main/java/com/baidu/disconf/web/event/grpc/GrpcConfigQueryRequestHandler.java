package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.core.common.remote.ConfigQueryRequest;
import com.baidu.disconf.core.common.remote.ConfigQueryResponse;
import com.baidu.disconf.core.common.remote.grpc.GrpcRequestHandler;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.service.config.service.ConfigFetchMgr;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/13 14:02
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class GrpcConfigQueryRequestHandler extends GrpcRequestHandler<ConfigQueryRequest, ConfigQueryResponse> {

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Autowired
    private ConfigFetchMgr configFetchMgr;


    @Override
    public ConfigQueryResponse handle(ConfigQueryRequest request) {

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
        configQueryResponse.setFileName(request.getFileName());
        configQueryResponse.setValue(value);

        return configQueryResponse;
    }
}