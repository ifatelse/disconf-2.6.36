package com.baidu.disconf.web.service.config.service;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.vo.ConfigHistoryReleaseVo;

import java.util.List;

/**
 * Created by knightliao on 15/12/25.
 */
public interface ConfigHistoryMgr {

    void createOne(Long configId, String oldValue, String newValue);

    /*
      * @description  查询现有历史记录最大的ID
      * @author Lethe
      * @date 2022/11/16 16:59
      */
    ConfigHistory findTopByOrderByIdDesc();

    /*
      * @description
      * @author Lethe
      * @date 2022/11/16 17:00
      */
    List<ConfigHistoryReleaseVo> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned);
}
