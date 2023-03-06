package com.baidu.disconf.web.event.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/2 15:26
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
// @Component
public class GrpcServer {


    @PostConstruct
    public void start() {

        Server server = ServerBuilder.forPort(10010 + 2000)
                .addService(new RequestAcceptor())
                .addService(new BiRequestAcceptor())
                .build();

    }
}
