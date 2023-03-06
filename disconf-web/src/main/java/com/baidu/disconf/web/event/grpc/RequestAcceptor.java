package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.grpc.auto.Message;
import com.baidu.disconf.core.grpc.auto.RequestGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:33
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class RequestAcceptor extends RequestGrpc.RequestImplBase {


    @Override
    public void request(Message request, StreamObserver<Message> responseObserver) {


        super.request(request, responseObserver);
    }
}
