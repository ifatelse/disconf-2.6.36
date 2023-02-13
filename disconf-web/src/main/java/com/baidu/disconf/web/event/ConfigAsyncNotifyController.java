package com.baidu.disconf.web.event;

import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.web.service.config.form.ConfForm;
import com.baidu.disconf.web.web.config.dto.ConfigFullModel;
import com.baidu.disconf.web.web.config.validator.ConfigValidator4Fetch;
import com.baidu.dsp.common.annotation.NoAuth;
import com.baidu.dsp.common.constant.WebConstants;
import com.baidu.dsp.common.exception.DocumentNotFoundException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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


    @NoAuth
    @RequestMapping(value = "/async/file", method = RequestMethod.GET)
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

        // 一定要由HTTP线程调用，否则离开后容器会立即发送响应
        final AsyncContext asyncContext = request.startAsync();
        // AsyncContext.setTimeout()的超时时间不准，所以只能自己控制
        asyncContext.setTimeout(0L);


        Set<String> fileNames = StringUtils.commaDelimitedListToSet(configModel.getKey());

        List<String> watchKeys = assembleAllWatchKeys(configModel, fileNames);

    }

    private List<String> assembleAllWatchKeys(ConfigFullModel configModel, Set<String> fileNames) {
        return fileNames.stream().map(fileName -> configModel.getApp().getId() + "-" + fileName + "-" + configModel.getVersion() + "-" + configModel.getEnv().getId()).collect(Collectors.toList());
    }


}
