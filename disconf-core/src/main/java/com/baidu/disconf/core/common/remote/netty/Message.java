package com.baidu.disconf.core.common.remote.netty;

import java.io.Serializable;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/3 11:26
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class Message implements Serializable {

    private static final long serialVersionUID = -793114318905250421L;

    private String msgType;

    private Object data;


    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "msgType='" + msgType + '\'' +
                ", data=" + data +
                '}';
    }
}
