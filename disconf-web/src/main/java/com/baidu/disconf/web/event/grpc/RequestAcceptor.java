package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.Response;
import com.baidu.disconf.core.common.remote.grpc.GrpcRequestHandler;
import com.baidu.disconf.core.common.utils.GrpcUtils;
import com.baidu.disconf.core.grpc.auto.Message;
import com.baidu.disconf.core.grpc.auto.RequestGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:33
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class RequestAcceptor extends RequestGrpc.RequestImplBase {

    private static final Logger logger = LoggerFactory.getLogger(RequestAcceptor.class);

    @Autowired
    private GrpcRequestHandlerRegistry grpcRequestHandlerRegistry;

    @Override
    public void request(Message message, StreamObserver<Message> responseObserver) {

        logger.info("received message: {}", message);

        String type = message.getType();

        GrpcRequestHandler requestHandler = grpcRequestHandlerRegistry.getByRequestType(type);

        // if(requestHandler == null){
        //
        //     responseObserver.onNext();
        //     responseObserver.onCompleted();
        //     return;
        // }

        Class<?> requestClassType = grpcRequestHandlerRegistry.getByRequestClassType(type);
        Object object = GrpcUtils.parse(message, requestClassType);
        Request request = (Request) object;

        Response response = requestHandler.handle(request);

        Message msgResponse = GrpcUtils.convert(response);

        responseObserver.onNext(msgResponse);
        responseObserver.onCompleted();
    }
}
