package com.baidu.disconf.web.event.grpc;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.utils.ReflectUtils;
import io.grpc.*;
import io.grpc.internal.ServerStream;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:26
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
@Component
public class GrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    @Autowired
    private RequestAcceptor requestAcceptor;

    @Autowired
    private BiRequestAcceptor biRequestAcceptor;

    @PostConstruct
    public void start() throws IOException {

        ServerInterceptor serverInterceptor = new ServerInterceptor() {
            @Override
            public <T, S> ServerCall.Listener<T> interceptCall(ServerCall<T, S> call, Metadata headers,
                                                               ServerCallHandler<T, S> next) {
                Context ctx = Context.current()
                        .withValue(CONTEXT_KEY_CONN_ID, call.getAttributes().get(TRANS_KEY_CONN_ID))
                        .withValue(CONTEXT_KEY_CONN_REMOTE_IP, call.getAttributes().get(TRANS_KEY_REMOTE_IP))
                        .withValue(CONTEXT_KEY_CONN_REMOTE_PORT, call.getAttributes().get(TRANS_KEY_REMOTE_PORT))
                        .withValue(CONTEXT_KEY_CONN_LOCAL_PORT, call.getAttributes().get(TRANS_KEY_LOCAL_PORT));
                // if ("BiRequestStream".equals(call.getMethodDescriptor().getServiceName())) {
                //     Channel internalChannel = getInternalChannel(call);
                //     ctx = ctx.withValue(CONTEXT_KEY_CHANNEL, internalChannel);
                // }
                return Contexts.interceptCall(ctx, call, headers, next);
            }
        };

        Server server = ServerBuilder.forPort(Constants.GRPC_PORT)
                .addService(ServerInterceptors.intercept(requestAcceptor, serverInterceptor))
                .addService(ServerInterceptors.intercept(biRequestAcceptor, serverInterceptor))
                .addTransportFilter(new ServerTransportFilter() {
                    @Override
                    public Attributes transportReady(Attributes transportAttrs) {
                        InetSocketAddress remoteAddress = (InetSocketAddress) transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
                        InetSocketAddress localAddress = (InetSocketAddress) transportAttrs.get(Grpc.TRANSPORT_ATTR_LOCAL_ADDR);
                        int remotePort = remoteAddress.getPort();
                        // grpc server port
                        int localPort = localAddress.getPort();

                        String remoteIp = remoteAddress.getAddress().getHostAddress();

                        // 设置一些值
                        Attributes attrWrapper = transportAttrs.toBuilder()
                                .set(TRANS_KEY_CONN_ID, System.currentTimeMillis() + "_" + remoteIp + "_" + remotePort)
                                .set(TRANS_KEY_REMOTE_IP, remoteIp)
                                .set(TRANS_KEY_REMOTE_PORT, remotePort)
                                .set(TRANS_KEY_LOCAL_PORT, localPort).build();

                        String connectionId = attrWrapper.get(TRANS_KEY_CONN_ID);
                        logger.info("Connection transportReady,connectionId = {} ", connectionId);
                        return attrWrapper;

                    }

                    @Override
                    public void transportTerminated(Attributes transportAttrs) {
                        String connectionId = transportAttrs.get(TRANS_KEY_CONN_ID);

                        logger.info("Connection transportTerminated,connectionId = {} ", connectionId);

                        ConfigChangeContext.removeListen(connectionId);

                        super.transportTerminated(transportAttrs);
                    }
                })
                .build();

        server.start();

        logger.info("Grpc server start...");

    }

    private Channel getInternalChannel(ServerCall serverCall) {
        ServerStream serverStream = (ServerStream) ReflectUtils.getFieldValue(serverCall, "stream");
        return (Channel) ReflectUtils.getFieldValue(serverStream, "channel");
    }


    static final Attributes.Key<String> TRANS_KEY_CONN_ID = Attributes.Key.create("conn_id");

    static final Attributes.Key<String> TRANS_KEY_REMOTE_IP = Attributes.Key.create("remote_ip");

    static final Attributes.Key<Integer> TRANS_KEY_REMOTE_PORT = Attributes.Key.create("remote_port");

    static final Attributes.Key<Integer> TRANS_KEY_LOCAL_PORT = Attributes.Key.create("local_port");

    static final Context.Key<String> CONTEXT_KEY_CONN_ID = Context.key("conn_id");

    static final Context.Key<String> CONTEXT_KEY_CONN_REMOTE_IP = Context.key("remote_ip");

    static final Context.Key<Integer> CONTEXT_KEY_CONN_REMOTE_PORT = Context.key("remote_port");

    static final Context.Key<Integer> CONTEXT_KEY_CONN_LOCAL_PORT = Context.key("local_port");

    static final Context.Key<Channel> CONTEXT_KEY_CHANNEL = Context.key("ctx_channel");
}
