package com.baidu.disconf.web.event;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.exception.DocumentNotFoundException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/14 16:53
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/notify")
public class ConfigAsyncNotifyController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigAsyncNotifyController.class);

    private final Multimap<String, DeferredResultWrapper> deferredResults =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @Autowired
    private AsyncLongPollService asyncLongPollService;


    @NoAuth
    @RequestMapping(value = "/change", method = RequestMethod.GET)
    @ResponseBody
    public void getFile(HttpServletRequest request, HttpServletResponse response, ConfForm confForm) {

        boolean hasError = false;

        // 校验
        ConfigFullModel configModel = null;
        try {
            configModel = configValidator4Fetch.verifyConfForm(confForm, false);
        } catch (Exception e) {
            logger.error(e.toString());
            hasError = true;
        }

        if (hasError) {
            throw new DocumentNotFoundException(confForm.toString());
        }

        String watchKey = assembleWatchKey(configModel);

        asyncLongPollService.doPollingConfig(request, response, watchKey);

    }

    private List<String> assembleAllWatchKeys(ConfigFullModel configModel, Set<String> fileNames) {
        return fileNames.stream().map(fileName -> configModel.getApp().getId() + "-" + fileName + "-" + configModel.getVersion() + "-" + configModel.getEnv().getId()).collect(Collectors.toList());
    }

    private String assembleWatchKey(ConfigFullModel configModel) {
        return configModel.getApp().getId() + Constants.CON_STRING +
                configModel.getVersion() + Constants.CON_STRING +
                configModel.getEnv().getId();
    }


}
