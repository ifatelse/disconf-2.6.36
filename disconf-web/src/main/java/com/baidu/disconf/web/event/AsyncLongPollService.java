package com.baidu.disconf.web.event;

import com.baidu.disconf.core.common.constants.Constants;
import com.baidu.disconf.core.common.json.ValueVo;
import com.baidu.disconf.core.common.utils.GsonUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description :
 * @Author : Lethe
 * @Date : 2022/11/21 15:16
 * @Version : 1.0
 * @Copyright : Copyright (c) 2022 All Rights Reserved
 **/
@Component
public class AsyncLongPollService extends AbstractEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AsyncLongPollService.class);

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Multimap<String, ClientLongPolling> clients =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

    private static final long TIMEOUT = 60 * 1000;

    public void doPollingConfig(HttpServletRequest request, HttpServletResponse response, String watchKey) {

        // 一定要由HTTP线程调用，否则离开后容器会立即发送响应
        final AsyncContext asyncContext = request.startAsync();
        // AsyncContext.setTimeout()的超时时间不准，所以只能自己控制
        asyncContext.setTimeout(0L);

        scheduler.execute(new ClientLongPolling(asyncContext, watchKey, TIMEOUT));

    }

    @Override
    public List<Class<? extends Event>> interest() {
        List<Class<? extends Event>> types = new ArrayList<>();
        types.add(ConfigChangeEvent.class);
        return types;
    }

    @Override
    void onEvent(Event event) {
        if (event instanceof ConfigChangeEvent) {
            ConfigChangeEvent evt = (ConfigChangeEvent) event;
            String confName = evt.confName;
            String confKey = evt.confKey;
            logger.info("change confName: {}, confKey: {}", confName, confKey);
            scheduler.execute(new ConfigChangeTask(confKey, confName));
        }
    }

    class ConfigChangeTask implements Runnable {

        final String watchKey;

        final String confName;

        public ConfigChangeTask(String watchKey, String confName) {
            this.watchKey = watchKey;
            this.confName = confName;
        }

        @Override
        public void run() {
            Collection<ClientLongPolling> clientLongPollings = clients.removeAll(watchKey);
            for (ClientLongPolling clientLongPolling : clientLongPollings) {
                clientLongPolling.sendResponse(confName);
            }
        }
    }


    class ClientLongPolling implements Runnable {

        final AsyncContext asyncContext;
        final String watchKey;
        final long timeoutTime;

        Future<?> asyncTimeoutFuture;

        public ClientLongPolling(AsyncContext asyncContext, String watchKey, long timeoutTime) {
            this.asyncContext = asyncContext;
            this.watchKey = watchKey;
            this.timeoutTime = timeoutTime;
        }

        @Override
        public void run() {
            asyncTimeoutFuture = scheduler.schedule(() -> {
                try {
                    clients.remove(watchKey, ClientLongPolling.this);
                    sendResponse(null);
                } catch (Throwable t) {
                    logger.error("long polling error:" + t.getMessage(), t.getCause());
                }

            }, timeoutTime, TimeUnit.MILLISECONDS);

            clients.put(watchKey, this);
        }

        void sendResponse(String confName) {
            /*
             *  取消超时任务
             */
            if (null != asyncTimeoutFuture) {
                asyncTimeoutFuture.cancel(false);
            }
            generateResponse(confName);
        }

        void generateResponse(String confName) {

            ValueVo valueVo = new ValueVo();


            if (null == confName) {
                /*
                 * 告诉容器发送HTTP响应
                 */
                // asyncContext.complete();
                // return;

                valueVo.setStatus(Constants.CONFIG_NO_CHANGE);
                valueVo.setMessage("");
                valueVo.setValue("");
            } else {
                valueVo.setStatus(Constants.CONFIG_CHANGE);
                valueVo.setMessage("change");
                valueVo.setValue(confName);
            }

            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

            try {
                // 禁用缓存
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                response.setHeader("Cache-Control", "no-cache,no-store");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(GsonUtils.toJson(valueVo));
                asyncContext.complete();
            } catch (Exception ex) {
                logger.error(ex.toString(), ex);
                asyncContext.complete();
            }
        }

    }


}
