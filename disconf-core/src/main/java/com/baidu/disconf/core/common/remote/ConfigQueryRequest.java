package com.baidu.disconf.core.common.remote;


import java.io.Serializable;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/13 15:57
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigQueryRequest extends Request implements Serializable {

    private static final long serialVersionUID = 267401832989338716L;

    private String appName;

    private String version;

    private String env;

    private String fileName;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
