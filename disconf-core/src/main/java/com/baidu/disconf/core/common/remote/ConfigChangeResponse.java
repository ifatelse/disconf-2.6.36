package com.baidu.disconf.core.common.remote;

import java.io.Serializable;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/23 10:08
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigChangeResponse extends Response implements Serializable {

    private static final long serialVersionUID = 7070259469817932328L;

    public ConfigChangeResponse() {
    }

    private Integer status;

    private String fileName;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
