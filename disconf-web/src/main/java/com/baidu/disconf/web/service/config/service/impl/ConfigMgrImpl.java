package com.baidu.disconf.web.service.config.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baidu.disconf.core.common.constants.DisConfigTypeEnum;
import com.baidu.disconf.web.common.Constants;
import com.baidu.disconf.web.config.ApplicationPropertyConfig;
import com.baidu.disconf.web.service.app.bo.App;
import com.baidu.disconf.web.service.app.service.AppMgr;
import com.baidu.disconf.web.service.config.bo.Config;
import com.baidu.disconf.web.service.config.dao.ConfigDao;
import com.baidu.disconf.web.service.config.form.ConfListForm;
import com.baidu.disconf.web.service.config.form.ConfNewItemForm;
import com.baidu.disconf.web.service.config.service.ConfigHistoryMgr;
import com.baidu.disconf.web.service.config.service.ConfigMgr;
import com.baidu.disconf.web.service.config.vo.ConfListVo;
import com.baidu.disconf.web.service.config.vo.MachineListVo;
import com.baidu.disconf.web.service.env.bo.Env;
import com.baidu.disconf.web.service.env.service.EnvMgr;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData;
import com.baidu.disconf.web.service.zookeeper.dto.ZkDisconfData.ZkDisconfDataItem;
import com.baidu.disconf.web.utils.CodeUtils;
import com.baidu.disconf.web.utils.DiffUtils;
import com.baidu.disconf.web.utils.MyStringUtils;
import com.baidu.dsp.common.constant.DataFormatConstants;
import com.baidu.dsp.common.utils.DataTransfer;
import com.baidu.dsp.common.utils.ServiceUtil;
import com.baidu.dsp.common.utils.email.LogMailBean;
import com.baidu.ub.common.db.DaoPageResult;
import com.github.knightliao.apollo.utils.data.GsonUtils;
import com.github.knightliao.apollo.utils.io.OsUtil;
import com.github.knightliao.apollo.utils.time.DateUtils;

/**
 * @author liaoqiqi
 * @version 2014-6-16
 */
@Service
public class ConfigMgrImpl implements ConfigMgr {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigMgrImpl.class);

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private AppMgr appMgr;

    @Autowired
    private EnvMgr envMgr;

    // @Autowired
    // private ZooKeeperDriver zooKeeperDriver;

    // @Autowired
    // private ZkDeployMgr zkDeployMgr;

    @Autowired
    private LogMailBean logMailBean;

    @Autowired
    private ApplicationPropertyConfig applicationPropertyConfig;

    @Autowired
    private ConfigHistoryMgr configHistoryMgr;

    /**
     * ??????APPid?????????????????????
     */
    @Override
    public List<String> getVersionListByAppEnv(Long appId, Long envId) {

        List<String> versionList = new ArrayList<String>();

        List<Config> configs = configDao.getConfByAppEnv(appId, envId);

        for (Config config : configs) {

            if (!versionList.contains(config.getVersion())) {
                versionList.add(config.getVersion());
            }
        }

        return versionList;
    }

    /**
     * ?????????????????????
     *
     * @param confListForm
     *
     * @return
     */
    public List<File> getDisconfFileList(ConfListForm confListForm) {

        List<Config> configList =
                configDao.getConfigList(confListForm.getAppId(), confListForm.getEnvId(), confListForm.getVersion(),true);

        // ???????????????????????????
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        curTime = "tmp" + File.separator + curTime;
        OsUtil.makeDirs(curTime);

        List<File> files = new ArrayList<File>();
        for (Config config : configList) {

            if (config.getType().equals(DisConfigTypeEnum.FILE.getType())) {

                File file = new File(curTime, config.getName());
                try {
                    FileUtils.writeByteArrayToFile(file, config.getValue().getBytes());
                } catch (IOException e) {
                    LOG.warn(e.toString());
                }

                files.add(file);
            }
        }

        return files;
    }

    /**
     * ????????????
     */
    @Override
    public DaoPageResult<ConfListVo> getConfigList(ConfListForm confListForm, boolean fetchZk,
                                                   final boolean getErrorMessage) {

        //
        // ???????????????
        //
        DaoPageResult<Config> configList = configDao.getConfigList(confListForm.getAppId(), confListForm.getEnvId(),
                confListForm.getVersion(),
                confListForm.getPage());

        //
        //
        //
        final App app = appMgr.getById(confListForm.getAppId());
        final Env env = envMgr.getById(confListForm.getEnvId());

        //
        //
        //
        final boolean myFetchZk = fetchZk;
        Map<String, ZkDisconfData> zkDataMap = new HashMap<String, ZkDisconfData>();
        if (myFetchZk) {
            // zkDataMap = zkDeployMgr.getZkDisconfDataMap(app.getName(), env.getName(), confListForm.getVersion());
        }
        final Map<String, ZkDisconfData> myzkDataMap = zkDataMap;

        //
        // ????????????
        //
        DaoPageResult<ConfListVo> configListVo =
                ServiceUtil.getResult(configList, new DataTransfer<Config, ConfListVo>() {

                    @Override
                    public ConfListVo transfer(Config input) {

                        String appNameString = app.getName();
                        String envName = env.getName();

                        ZkDisconfData zkDisconfData = null;
                        if (myzkDataMap != null && myzkDataMap.keySet().contains(input.getName())) {
                            zkDisconfData = myzkDataMap.get(input.getName());
                        }
                        ConfListVo configListVo = convert(input, appNameString, envName, zkDisconfData);

                        // ???????????????????????????, ????????????????????????(?????????????????????)
                        if (!myFetchZk && !getErrorMessage) {

                            // ?????? value ????????? ""
                            configListVo.setValue("");
                            configListVo.setMachineList(new ArrayList<ZkDisconfData.ZkDisconfDataItem>());
                        }

                        return configListVo;
                    }
                });

        return configListVo;
    }

    /**
     * ??????ZK data
     */
    private MachineListVo getZkData(List<ZkDisconfDataItem> datalist, Config config) {

        int errorNum = 0;
        for (ZkDisconfDataItem zkDisconfDataItem : datalist) {

            if (config.getType().equals(DisConfigTypeEnum.FILE.getType())) {

                List<String> errorKeyList = compareConfig(zkDisconfDataItem.getValue(), config.getValue());

                if (errorKeyList.size() != 0) {
                    zkDisconfDataItem.setErrorList(errorKeyList);
                    errorNum++;
                }
            } else {

                //
                // ?????????
                //

                if (zkDisconfDataItem.getValue().trim().equals(config.getValue().trim())) {

                } else {
                    List<String> errorKeyList = new ArrayList<String>();
                    errorKeyList.add(config.getValue().trim());
                    zkDisconfDataItem.setErrorList(errorKeyList);
                    errorNum++;
                }
            }
        }

        MachineListVo machineListVo = new MachineListVo();
        machineListVo.setDatalist(datalist);
        machineListVo.setErrorNum(errorNum);
        machineListVo.setMachineSize(datalist.size());

        return machineListVo;
    }

    /**
     * ?????????????????????
     *
     * @param config
     *
     * @return
     */
    private ConfListVo convert(Config config, String appNameString, String envName, ZkDisconfData zkDisconfData) {

        ConfListVo confListVo = new ConfListVo();

        confListVo.setConfigId(config.getId());
        confListVo.setAppId(config.getAppId());
        confListVo.setAppName(appNameString);
        confListVo.setEnvName(envName);
        confListVo.setEnvId(config.getEnvId());
        confListVo.setCreateTime(config.getCreateTime());
        confListVo.setModifyTime(config.getUpdateTime().substring(0, 12));
        confListVo.setKey(config.getName());
        // StringEscapeUtils.escapeHtml escape
        confListVo.setValue(CodeUtils.unicodeToUtf8(config.getValue()));
        confListVo.setVersion(config.getVersion());
        confListVo.setType(DisConfigTypeEnum.getByType(config.getType()).getModelName());
        confListVo.setTypeId(config.getType());

        //
        //
        //
        if (zkDisconfData != null) {

            confListVo.setMachineSize(zkDisconfData.getData().size());

            List<ZkDisconfDataItem> datalist = zkDisconfData.getData();

            MachineListVo machineListVo = getZkData(datalist, config);

            confListVo.setErrorNum(machineListVo.getErrorNum());
            confListVo.setMachineList(machineListVo.getDatalist());
            confListVo.setMachineSize(machineListVo.getMachineSize());
        }

        return confListVo;
    }

    /**
     *
     */
    private List<String> compareConfig(String zkData, String dbData) {

        List<String> errorKeyList = new ArrayList<String>();

        Properties prop = new Properties();
        try {
            prop.load(IOUtils.toInputStream(dbData));
        } catch (Exception e) {
            LOG.error(e.toString());
            errorKeyList.add(zkData);
            return errorKeyList;
        }

        Map<String, String> zkMap = GsonUtils.parse2Map(zkData);
        for (String keyInZk : zkMap.keySet()) {

            Object valueInDb = prop.get(keyInZk);
            String zkDataStr = zkMap.get(keyInZk);

            // convert zk data to utf-8
            //zkMap.put(keyInZk, CodeUtils.unicodeToUtf8(zkDataStr));

            try {

                if ((zkDataStr == null && valueInDb != null) || (zkDataStr != null && valueInDb == null)) {
                    errorKeyList.add(keyInZk);

                } else {

                    zkDataStr = zkDataStr.trim();
                    boolean isEqual = true;

                    if (MyStringUtils.isDouble(zkDataStr) && MyStringUtils.isDouble(valueInDb.toString())) {

                        if (Math.abs(Double.parseDouble(zkDataStr) - Double.parseDouble(valueInDb.toString())) >
                                0.001d) {
                            isEqual = false;
                        }

                    } else {
                        if (!zkDataStr.equals(valueInDb.toString().trim())) {
                            isEqual = false;
                        }
                    }

                    if (!isEqual) {
                        errorKeyList
                                .add(keyInZk + "\t" + DiffUtils.getDiffSimple(zkDataStr, valueInDb.toString().trim()));
                    }
                }

            } catch (Exception e) {

                LOG.warn(e.toString() + " ; " + keyInZk + " ; " + zkMap.get(keyInZk) + " ; " + valueInDb);
            }
        }

        return errorKeyList;
    }

    /**
     * ?????? ??????ID??????????????????
     */
    @Override
    public ConfListVo getConfVo(Long configId) {
        Config config = configDao.get(configId);

        App app = appMgr.getById(config.getAppId());
        Env env = envMgr.getById(config.getEnvId());

        return convert(config, app.getName(), env.getName(), null);
    }

    /**
     * ?????? ??????ID??????ZK????????????
     */
    @Override
    public MachineListVo getConfVoWithZk(Long configId) {

        Config config = configDao.get(configId);

        App app = appMgr.getById(config.getAppId());
        Env env = envMgr.getById(config.getEnvId());

        //
        //
        //

        DisConfigTypeEnum disConfigTypeEnum = DisConfigTypeEnum.FILE;
        if (config.getType().equals(DisConfigTypeEnum.ITEM.getType())) {
            disConfigTypeEnum = DisConfigTypeEnum.ITEM;
        }

        ZkDisconfData zkDisconfData = null;

        // ZkDisconfData zkDisconfData = zkDeployMgr.getZkDisconfData(app.getName(), env.getName(), config.getVersion(),
        //         disConfigTypeEnum, config.getName());

        if (zkDisconfData == null) {
            return new MachineListVo();
        }

        MachineListVo machineListVo = getZkData(zkDisconfData.getData(), config);
        return machineListVo;
    }

    /**
     * ????????????ID????????????
     */
    @Override
    public Config getConfigById(Long configId) {

        return configDao.get(configId);
    }

    /**
     * ?????? ?????????/???????????? ??????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public String updateItemValue(Long configId, String value) {

        Config config = getConfigById(configId);
        String oldValue = config.getValue();

        //
        // ????????????????????? encode to db
        //
        configDao.updateValue(configId, CodeUtils.utf8ToUnicode(value));
        configHistoryMgr.createOne(configId, oldValue, CodeUtils.utf8ToUnicode(value));

        //
        // ??????????????????
        //
        String toEmails = appMgr.getEmails(config.getAppId());

        if (applicationPropertyConfig.isEmailMonitorOn()) {
            boolean isSendSuccess = logMailBean.sendHtmlEmail(toEmails,
                    " config update", DiffUtils.getDiff(CodeUtils.unicodeToUtf8(oldValue),
                            value,
                            config.toString(),
                            getConfigUrlHtml(config)));
            if (isSendSuccess) {
                return "?????????????????????????????????";
            } else {
                return "?????????????????????????????????????????????????????????";
            }
        }

        return "????????????";
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private String getConfigUrlHtml(Config config) {

        return "<br/>??????<a href='http://" + applicationPropertyConfig.getDomain() + "/modifyFile.html?configId=" +
                config.getId() + "'> ?????? </a> ????????????<br/>";
    }

    /**
     * ????????????????????????
     *
     * @param newValue
     * @param identify
     *
     * @return
     */
    private String getNewValue(String newValue, String identify, String htmlClick) {

        String contentString = StringEscapeUtils.escapeHtml4(identify) + "<br/>" + htmlClick + "<br/><br/> ";

        String data = "<br/><br/><br/><span style='color:#FF0000'>New value:</span><br/>";
        contentString = contentString + data + StringEscapeUtils.escapeHtml4(newValue);

        return contentString;
    }

    /**
     * ??????Zookeeper, ???????????????????????????,?????????????????????????????????????????????
     */
    @Override
    public void notifyZookeeper(Long configId) {

        ConfListVo confListVo = getConfVo(configId);

        if (confListVo.getTypeId().equals(DisConfigTypeEnum.FILE.getType())) {

            // zooKeeperDriver.notifyNodeUpdate(confListVo.getAppName(), confListVo.getEnvName(), confListVo.getVersion(),
            //         confListVo.getKey(), GsonUtils.toJson(confListVo.getValue()),
            //         DisConfigTypeEnum.FILE);

        } else {

            // zooKeeperDriver.notifyNodeUpdate(confListVo.getAppName(), confListVo.getEnvName(), confListVo.getVersion(),
            //         confListVo.getKey(), confListVo.getValue(), DisConfigTypeEnum.ITEM);
        }

    }

    /**
     * ???????????????
     */
    @Override
    public String getValue(Long configId) {
        return configDao.getValue(configId);
    }

    /**
     * ????????????
     */
    @Override
    public void newConfig(ConfNewItemForm confNewForm, DisConfigTypeEnum disConfigTypeEnum) {

        Config config = new Config();

        config.setAppId(confNewForm.getAppId());
        config.setEnvId(confNewForm.getEnvId());
        config.setName(confNewForm.getKey());
        config.setType(disConfigTypeEnum.getType());
        config.setVersion(confNewForm.getVersion());
        config.setValue(CodeUtils.utf8ToUnicode(confNewForm.getValue()));
        config.setStatus(Constants.STATUS_NORMAL);

        // ??????
        String curTime = DateUtils.format(new Date(), DataFormatConstants.COMMON_TIME_FORMAT);
        config.setCreateTime(curTime);
        config.setUpdateTime(curTime);

        configDao.create(config);
        configHistoryMgr.createOne(config.getId(), "", config.getValue());

        // ??????????????????
        //
        String toEmails = appMgr.getEmails(config.getAppId());
        if (applicationPropertyConfig.isEmailMonitorOn() == true) {
            logMailBean.sendHtmlEmail(toEmails, " config new", getNewValue(confNewForm.getValue(), config.toString(),
                    getConfigUrlHtml(config)));
        }
    }

    /**
     * ????????????
     *
     * @param configId
     */
    @Override
    public void delete(Long configId) {

        Config config = configDao.get(configId);
        configHistoryMgr.createOne(configId, config.getValue(), "");

        configDao.deleteItem(configId);
    }

}
