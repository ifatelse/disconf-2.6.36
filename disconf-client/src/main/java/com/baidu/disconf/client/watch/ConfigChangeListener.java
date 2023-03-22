package com.baidu.disconf.client.watch;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/16 18:08
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public interface ConfigChangeListener {

    void configChange(String fileName);

}
