package com.baidu.disconf.client.watch.inner;

import com.baidu.disconf.client.common.model.DisConfCommonModel;
import com.baidu.disconf.client.core.processor.DisconfCoreProcessor;
import com.baidu.disconf.client.fetcher.FetcherMgr;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/12/6 11:21
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
public class RemoteConfigRepository {

    final public FetcherMgr fetcherMgr;
    final public DisConfCommonModel disConfCommonModel;

    public RemoteConfigRepository(FetcherMgr fetcherMgr, DisConfCommonModel disConfCommonModel) {
        this.fetcherMgr = fetcherMgr;
        this.disConfCommonModel = disConfCommonModel;
    }

}
