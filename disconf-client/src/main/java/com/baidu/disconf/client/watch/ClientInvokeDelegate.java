package com.baidu.disconf.client.watch;

import com.baidu.disconf.client.common.model.DisconfCenterFile;

import java.util.Collection;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/16 16:01
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public interface ClientInvokeDelegate {

    void configLoad(String fileName);

    void configListen(ConfigChangeListener configChangeListener);

}
