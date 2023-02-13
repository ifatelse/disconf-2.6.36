package com.baidu.disconf.web.service.config.dao;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.vo.ConfigHistoryReleaseVo;
import com.baidu.unbiz.common.genericdao.dao.BaseDao;

import java.util.List;

/**
 * Created by knightliao on 15/12/25.
 */

public interface ConfigHistoryDao extends BaseDao<Long, ConfigHistory> {

    ConfigHistory findTopByOrderByIdDesc();

    List<ConfigHistoryReleaseVo> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned);

}
