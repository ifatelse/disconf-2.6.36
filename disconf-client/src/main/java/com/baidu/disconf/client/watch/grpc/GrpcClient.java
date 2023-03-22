package com.baidu.disconf.client.watch.grpc;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.Response;
import com.baidu.disconf.core.common.remote.grpc.ConnectionSetupRequest;
import com.baidu.disconf.core.grpc.auto.BiRequestStreamGrpc;
import com.baidu.disconf.core.grpc.auto.Message;
import com.baidu.disconf.core.grpc.auto.RequestGrpc;
import io.grpc.CompressorRegistry;
import io.grpc.DecompressorRegistry;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/10 17:54
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class GrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(GrpcClient.class);

    private GrpcConnection grpcConnection;

    static {
        ResponseRegistry.init();
    }


    public void start() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", Constants.GRPC_PORT)
                .compressorRegistry(CompressorRegistry.getDefaultInstance())
                .decompressorRegistry(DecompressorRegistry.getDefaultInstance())
                .maxInboundMessageSize(10 * 1024 * 1024)
                .keepAliveTime(6 * 60 * 1000, TimeUnit.MILLISECONDS).usePlaintext();

        ManagedChannel managedChannel = channelBuilder.build();

        RequestGrpc.RequestFutureStub requestFutureStub = RequestGrpc.newFutureStub(managedChannel);

        BiRequestStreamGrpc.BiRequestStreamStub biRequestStreamStub = BiRequestStreamGrpc.newStub(managedChannel);

        StreamObserver<Message> messageStreamObserver = bindRequestStream(biRequestStreamStub);

        GrpcConnection grpcConnection = new GrpcConnection();
        grpcConnection.setChannel(managedChannel);
        grpcConnection.setRequestFutureStub(requestFutureStub);
        grpcConnection.setMessageStreamObserver(messageStreamObserver);

        ConnectionSetupRequest setupRequest = new ConnectionSetupRequest();
        grpcConnection.sendRequest(setupRequest);

        this.grpcConnection = grpcConnection;

        logger.info("Grpc client start...");

    }

    private StreamObserver<Message> bindRequestStream(BiRequestStreamGrpc.BiRequestStreamStub biRequestStreamStub) {

        return biRequestStreamStub.biRequestStream(new StreamObserver<Message>() {
            @Override
            public void onNext(Message message) {
                logger.info("接受到服务端的message信息为：{}", message);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

    }


    public Response request(Request request) {
        return grpcConnection.request(request);
    }

}
