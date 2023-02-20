package com.baidu.disconf.core.common.remote;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/2/14 15:59
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class ConfigQueryResponse extends Response {

    private String fileName;

    private String value;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
