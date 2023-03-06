package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.grpc.auto.BiRequestStreamGrpc;
import com.baidu.disconf.core.grpc.auto.Message;
import io.grpc.stub.StreamObserver;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:37
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class BiRequestAcceptor extends BiRequestStreamGrpc.BiRequestStreamImplBase {

    @Override
    public StreamObserver<Message> biRequestStream(StreamObserver<Message> responseObserver) {
        return super.biRequestStream(responseObserver);
    }
}
