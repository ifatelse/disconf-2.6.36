package com.baidu.disconf.web.event;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.core.common.utils.DisconfThreadFactory;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/14 16:53
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
@Controller
@RequestMapping(WebConstants.API_PREFIX + "/notify")
public class ConfigNotifyController extends AbstractEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ConfigNotifyController.class);

    private final Multimap<String, DeferredResultWrapper> deferredResults =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

    ExecutorService notifyExecutorService = Executors.newSingleThreadExecutor(DisconfThreadFactory.create("ConfigDeferredNotifyController", true));


    @Autowired
    private ConfigValidator4Fetch configValidator4Fetch;

    @NoAuth
    @RequestMapping(value = "/change", method = RequestMethod.GET)
    @ResponseBody
    public DeferredResult<ValueVo> getFile(ConfForm confForm) {

        logger.info("Request ConfForm: " + confForm);

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

        DeferredResultWrapper deferredResultWrapper = new DeferredResultWrapper();

        deferredResultWrapper.onTimeout(() -> {
            logger.debug("LongPoll.TimeOutKeys:{}", watchKey);
        });

        deferredResultWrapper.onCompletion(() -> {
            logger.debug("LongPoll.CompletedKeys:{}", watchKey);
            deferredResults.remove(watchKey, deferredResultWrapper);
        });

        deferredResults.put(watchKey, deferredResultWrapper);

        return deferredResultWrapper.getResult();
    }

    private String assembleWatchKey(ConfigFullModel configModel) {
        return configModel.getApp().getId() + Constants.CON_STRING +
                configModel.getVersion() + Constants.CON_STRING +
                configModel.getEnv().getId();
    }


    private String assembleWatchKey(Long appId, String confName, String version, Long envId) {
        return appId + Constants.CON_STRING + confName + Constants.CON_STRING + version + Constants.CON_STRING + envId;
    }


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
            List<DeferredResultWrapper> deferredResultList = Lists.newArrayList(deferredResults.get(confKey));
            logger.info("change notify:{}", deferredResultList.size());
            for (DeferredResultWrapper wrapper : deferredResultList) {
                ValueVo valueVo = new ValueVo();
                valueVo.setStatus(Constants.CONFIG_CHANGE);
                valueVo.setMessage("change");
                valueVo.setValue(confName);
                wrapper.setResult(valueVo);
            }
        }
    }


}
