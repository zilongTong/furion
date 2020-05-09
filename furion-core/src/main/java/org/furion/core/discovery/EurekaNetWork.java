package org.furion.core.discovery;


import org.furion.core.bean.eureka.Server;
import org.furion.core.utils.AnalysisXmlUtil;
import org.furion.core.bean.eureka.ApplicationBean;
import org.furion.core.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.dyuproject.protostuff.CollectionSchema.MessageFactories.ArrayList;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-17
 */
@Slf4j
public class EurekaNetWork {

    private static AtomicInteger pos = new AtomicInteger(0);

//    public static final ApplicationBean applicationCache = new ApplicationBean();

    private static String[] eurekaUrls;
    private static volatile Map<String,List<Server>> serverListMap = new HashMap<>();
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,r ->
         new Thread(r,"eureka thread")
    );

    static {
        //这两项从配置文件读取
        eurekaUrls = "http://eureka-test.zmlearn.com/eureka/".split(",");
        long timeGap = 10l;
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    EurekaNetWork.fetchAllService();
//                    System.out.println("eureka finish");
                } catch (Exception e) {
                    log.error("fetchRegisterThread fetch eureka application error", e);
                }
            }
        },0,timeGap,TimeUnit.SECONDS);
    }

    public static void stop() {
        executorService.shutdown();
    }

//    public static ApplicationBean getApplicationCache() {
//        if (applicationCache != null && !CollectionUtils.isEmpty(applicationCache.getServiceBeans())) {
//            return applicationCache;
//        }
//        return null;
//    }
    public static List<Server> getServerList(String key) {
        if (serverListMap == null) {
            fetchAllService();
            if(serverListMap == null)
                return null;
        }
        return serverListMap.get(key.toUpperCase());
    }


    public static synchronized void fetchAllService() {
        ApplicationBean bean;
        try {
            String url = roundRobbin();
            String resultXMl = OkHttpUtil.get(url.concat("apps"), null);
            bean = (ApplicationBean) AnalysisXmlUtil.convertXmlStrToObject(ApplicationBean.class, resultXMl);
            if (bean != null) {
                Map<String, List<Server>> newMap = new HashMap();
                bean.getServiceBeans().forEach(serviceBean -> {
                    newMap.put(serviceBean.getServiceName(),new ArrayList<>());
                    final List<Server> serverList = newMap.get(serviceBean.getServiceName());
                    serviceBean.getInstanceBeans().forEach(instanceBean -> {
                        if("UP".equals(instanceBean.getStatus()) && !"10.28.150.218".equals(instanceBean.getHostName())) {
                            Server server = new Server(instanceBean.getHostName(), Integer.valueOf(instanceBean.getPort()));
                            serverList.add(server);
                        }
                    });
                });
                serverListMap = newMap;
            }

        } catch (Exception e) {
            log.error("EurekaNetWork error", e);
        }
    }

    private static String roundRobbin() {
        String server = null;
        try {
            if (pos.get() >= eurekaUrls.length) {
                pos.getAndSet(0);
            }
            server = eurekaUrls[pos.get()];
            pos.getAndIncrement();
        } catch (Exception e) {
            log.error("roundRobbin exception", e);
        }
        return server;
    }

}
