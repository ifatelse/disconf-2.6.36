package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.grpc.auto.Message;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/13 16:35
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class ConnectionManager {


    public boolean register(String connectionId, StreamObserver<Message> responseObserver) {
        return false;
    }


}
