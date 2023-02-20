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

    // final public DisconfCoreProcessor disconfCoreMgr;
    final public FetcherMgr fetcherMgr;
    final public DisConfCommonModel disConfCommonModel;
    // final public DisconfSysUpdateCallback disconfSysUpdateCallback;

    // public RemoteConfigRepository(DisconfCoreProcessor disconfCoreMgr, FetcherMgr fetcherMgr, DisConfCommonModel disConfCommonModel, DisconfSysUpdateCallback disconfSysUpdateCallback) {
    //     this.disconfCoreMgr = disconfCoreMgr;
    //     this.fetcherMgr = fetcherMgr;
    //     this.disConfCommonModel = disConfCommonModel;
    //     this.disconfSysUpdateCallback = disconfSysUpdateCallback;
    // }

    public RemoteConfigRepository(FetcherMgr fetcherMgr, DisConfCommonModel disConfCommonModel) {
        this.fetcherMgr = fetcherMgr;
        this.disConfCommonModel = disConfCommonModel;
    }



}
