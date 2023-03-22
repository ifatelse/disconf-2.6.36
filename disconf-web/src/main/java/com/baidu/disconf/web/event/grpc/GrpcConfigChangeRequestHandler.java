package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.ConfigChangeRequest;
import com.baidu.disconf.core.common.remote.ConfigChangeResponse;
import com.baidu.disconf.core.common.remote.grpc.GrpcRequestHandler;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.baidu.disconf.web.event.grpc.GrpcServer.CONTEXT_KEY_CONN_ID;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/14 14:11
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class GrpcConfigChangeRequestHandler extends GrpcRequestHandler<ConfigChangeRequest, ConfigChangeResponse> {

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Override
    public ConfigChangeResponse handle(ConfigChangeRequest request) {

        ConfForm confForm = new ConfForm();
        confForm.setApp(request.getAppName());
        confForm.setEnv(request.getEnv());
        confForm.setKey(request.getFileName());
        confForm.setVersion(request.getVersion());

        ConfigFullModel configModel = configValidator4Fetch.verifyConfForm(confForm, false);

        String watchKey = configModel.getApp().getId() + Constants.CON_STRING + configModel.getVersion() + Constants.CON_STRING + configModel.getEnv().getId();

        String connectionId = CONTEXT_KEY_CONN_ID.get();

        ConfigChangeContext.listenKey(watchKey, connectionId);

        return new ConfigChangeResponse();
    }


}
