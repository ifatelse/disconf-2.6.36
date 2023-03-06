package com.baidu.disconf.core.common.remote;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 15:55
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class Request {

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
