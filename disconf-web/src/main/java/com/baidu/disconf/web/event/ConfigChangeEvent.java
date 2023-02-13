package com.baidu.disconf.web.event;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/18 17:42
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class ConfigChangeEvent implements Event {

    final public String confName;
    final public String confKey;

    public ConfigChangeEvent(String confName, String confKey) {
        this.confName = confName;
        this.confKey = confKey;
    }
}
