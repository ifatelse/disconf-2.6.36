package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.remote.grpc.ConnectionSetupRequest;
import com.baidu.disconf.core.common.utils.GrpcUtils;
import com.baidu.disconf.core.grpc.auto.BiRequestStreamGrpc;
import com.baidu.disconf.core.grpc.auto.Message;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.baidu.disconf.web.event.grpc.GrpcServer.*;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:37
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class BiRequestAcceptor extends BiRequestStreamGrpc.BiRequestStreamImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BiRequestAcceptor.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Override
    public StreamObserver<Message> biRequestStream(StreamObserver<Message> responseObserver) {

        StreamObserver<Message> messageStreamObserver = new StreamObserver<Message>() {

            final String connectionId = CONTEXT_KEY_CONN_ID.get();

            String remoteIp = CONTEXT_KEY_CONN_REMOTE_IP.get();

            final int remotePort = CONTEXT_KEY_CONN_REMOTE_PORT.get();

            final Integer localPort = CONTEXT_KEY_CONN_LOCAL_PORT.get();


            @Override
            public void onNext(Message message) {
                logger.info("received msg: {}", message);
                logger.info("received remoteIp: {}, remotePort: {}, localPort: {}", remoteIp, remotePort, localPort);
                String type = message.getType();
                if (Objects.equals(type, ConnectionSetupRequest.class.getSimpleName())) {
                    ConfigChangeContext.listenStream(connectionId, responseObserver);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };

        return messageStreamObserver;
    }
}
