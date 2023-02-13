package com.baidu.disconf.client.fetcher;

import com.baidu.disconf.core.common.json.ValueVo;

/**
 * 下载模块
 *
 * @author liaoqiqi
 * @version 2014-6-12
 */
public interface FetcherMgr {

    /**
     * 根据 URL 从远程 获取Value值
     */
    String getValueFromServer(String url) throws Exception;

    /**
     * 下载配置文件, remoteUrl是目标 url, 下载到预定义的文件夹，并 下载到 targetDirPath 目录下
     *
     * @throws Exception
     */
    String downloadFileFromServer(String url, String fileName, String targetDirPath) throws Exception;

    /**
     * 释放资源
     */
    void release();

    /*
      * @description  获取fileName是否发生变化
      * @author Lethe
      * @date 2022/11/14 21:35
      */
    ValueVo getChangeFileFromServer(String url);
}
