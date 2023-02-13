package com.baidu.disconf.web.event;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/17 11:26
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 * @Version : 1.0
 **/
public class DeferredResultWrapper {

    private static final long TIMEOUT = 60 * 1000;//60 seconds

    private static final ValueVo
            NOT_MODIFIED_RESPONSE = new ValueVo(Constants.CONFIG_NO_CHANGE,"","");


    private final DeferredResult<ValueVo> result;


    public DeferredResultWrapper() {
        result = new DeferredResult<>(TIMEOUT, NOT_MODIFIED_RESPONSE);
    }

    public void onTimeout(Runnable timeoutCallback) {
        result.onTimeout(timeoutCallback);
    }

    public void onCompletion(Runnable completionCallback) {
        result.onCompletion(completionCallback);
    }


    public void setResult(ValueVo valueVo) {
        result.setResult(valueVo);
    }

    public DeferredResult<ValueVo> getResult() {
        return result;
    }
}
