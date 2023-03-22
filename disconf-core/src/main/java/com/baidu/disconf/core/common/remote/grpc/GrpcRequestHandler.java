package com.baidu.disconf.core.common.remote.grpc;

import com.baidu.disconf.core.common.remote.Request;
import com.baidu.disconf.core.common.remote.Response;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2023/3/13 13:35
 * @Version : 1.0
 * @Copyright : Copyright (c) 2023 All Rights Reserved
 **/
public abstract class GrpcRequestHandler<T extends Request, S extends Response> {

    public abstract S handle(T request);

}
