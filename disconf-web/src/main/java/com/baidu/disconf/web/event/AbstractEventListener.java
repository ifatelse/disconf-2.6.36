package com.baidu.disconf.web.event;

import java.util.List;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/18 17:37
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public abstract class AbstractEventListener {

    public AbstractEventListener() {
        /*
         * automatic register
         */
        EventDispatcher.addEventListener(this);
    }

    /**
     * 感兴趣的事件列表
     */
    abstract public List<Class<? extends Event>> interest();


    abstract void onEvent(Event event);

}
