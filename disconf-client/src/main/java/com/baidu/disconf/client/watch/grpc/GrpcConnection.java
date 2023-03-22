package com.baidu.disconf.client.watch.grpc;

import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.Response;
import com.baidu.disconf.core.common.utils.GrpcUtils;
import com.baidu.disconf.core.grpc.auto.Message;
import com.baidu.disconf.core.grpc.auto.RequestGrpc;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/10 18:01
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public class GrpcConnection {

    protected ManagedChannel channel;

    protected RequestGrpc.RequestFutureStub requestFutureStub;

    protected StreamObserver<Message> messageStreamObserver;


    public void sendRequest(Request request) {
        Message convert = GrpcUtils.convert(request);
        messageStreamObserver.onNext(convert);
    }

    public Response request(Request request) {

        Message message = GrpcUtils.convert(request);

        ListenableFuture<Message> requestFuture = requestFutureStub.request(message);

        Message grpcResponse;
        try {
            grpcResponse = requestFuture.get(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String responseType = grpcResponse.getType();
        Class<?> responseClass = ResponseRegistry.getClassByType(responseType);

        return (Response) GrpcUtils.parse(grpcResponse, responseClass);

    }


    public ManagedChannel getChannel() {
        return channel;
    }

    public void setChannel(ManagedChannel channel) {
        this.channel = channel;
    }

    public RequestGrpc.RequestFutureStub getRequestFutureStub() {
        return requestFutureStub;
    }

    public void setRequestFutureStub(RequestGrpc.RequestFutureStub requestFutureStub) {
        this.requestFutureStub = requestFutureStub;
    }

    public StreamObserver<Message> getMessageStreamObserver() {
        return messageStreamObserver;
    }

    public void setMessageStreamObserver(StreamObserver<Message> messageStreamObserver) {
        this.messageStreamObserver = messageStreamObserver;
    }
}
