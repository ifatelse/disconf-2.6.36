package com.baidu.disconf.web.service.config.dao.impl;

import com.baidu.disconf.web.service.config.vo.ConfigHistoryReleaseVo;
import com.baidu.dsp.common.dao.Columns;
import com.baidu.unbiz.common.genericdao.operator.Match;
import com.baidu.unbiz.common.genericdao.operator.Order;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.baidu.disconf.web.service.config.bo.ConfigHistory;
import com.baidu.disconf.web.service.config.dao.ConfigHistoryDao;
import com.baidu.dsp.common.dao.AbstractDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knightliao on 15/12/25.
 */
@Service
public class ConfigHistoryDaoImpl extends AbstractDao<Long, ConfigHistory> implements ConfigHistoryDao {

    @Override
    public ConfigHistory findTopByOrderByIdDesc() {
        // String sql = "select * from config_history order by id desc limit 1";
        // List<ConfigHistory> bySQL = findBySQL(sql, null);
        // if (CollectionUtils.isEmpty(bySQL)) {
        //     return null;
        // }
        // return bySQL.get(0);

        List<Order> orders = new ArrayList<Order>();
        orders.add(new Order("id", false));
        List<ConfigHistory> configHistories = find(Lists.newArrayList(), orders, 0, 1);
        if (CollectionUtils.isEmpty(configHistories)) {
            return null;
        }
        return configHistories.get(0);
    }

    @Override
    public List<ConfigHistoryReleaseVo> findFirst500ByIdGreaterThanOrderByIdAsc(long maxIdScanned) {
        // String sql = "select " +
        //         "    ch.id, " +
        //         "    a.app_id as appId, " +
        //         "    a.name as appName, " +
        //         "    c.name as fileName, " +
        //         "    c.version, " +
        //         "    e.env_id as envId, " +
        //         "    e.name as env, " +
        //         "    concat(a.app_id, '+', c.name, '+', c.version, '+', e.env_id) as confKey " +
        //         " from " +
        //         "    config_history ch " +
        //         "    left join config c on ch.config_id = c.config_id " +
        //         "    left join app a on a.app_id = c.app_id " +
        //         "    left join env e on e.env_id = c.env_id " +
        //         " where " +
        //         "    ch.id > ? " +
        //         " order by " +
        //         "    ch.id ";
        String sql = "select " +
                "    ch.id, " +
                "    c.name as confName, " +
                "    concat(c.app_id, '+', c.version, '+', c.env_id) as confKey " +
                " from " +
                "    config c " +
                "    left join config_history ch on ch.config_id = c.config_id " +
                " where " +
                "    ch.id > ? " +
                " order by " +
                "    ch.id ";

        List<ConfigHistoryReleaseVo> configHistoryReleaseVos = jdbcTemplate.query(sql, new Object[]{maxIdScanned}, new BeanPropertyRowMapper(ConfigHistoryReleaseVo.class));
        return configHistoryReleaseVos;
    }

}
