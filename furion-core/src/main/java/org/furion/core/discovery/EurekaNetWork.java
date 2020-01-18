package org.furion.core.discovery;


import org.furion.core.utils.AnalysisXmlUtil;
import org.furion.core.bean.eureka.ApplicationBean;
import org.furion.core.utils.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-17
 */
@Slf4j
public class EurekaNetWork {

    private static AtomicInteger pos = new AtomicInteger(0);

    public static final ApplicationBean applicationCache = new ApplicationBean();

    private static String[] eurekaUrls;
    private static String eurekaUrl;

    public EurekaNetWork(String eurekaUrl) {
        this.eurekaUrl = eurekaUrl;
    }

    static {
        eurekaUrls = eurekaUrl.split(",");
    }

    public static ApplicationBean getApplicationCache() {
        if (applicationCache != null && !CollectionUtils.isEmpty(applicationCache.getServiceBeans())) {
            return applicationCache;
        }
        return fetchAllService();
    }

    public static synchronized ApplicationBean fetchAllService() {
        ApplicationBean bean;
        try {
            String url = roundRobbin();
            String resultXMl = OkHttpUtil.get(url.concat("apps"), null);
            bean = (ApplicationBean) AnalysisXmlUtil.convertXmlStrToObject(ApplicationBean.class, resultXMl);
            if (bean == null) {
                return bean;
            }
            applicationCache.setServiceBeans(bean.getServiceBeans());
        } catch (Exception e) {
            log.error("EurekaNetWork error", e);
        }
        return applicationCache;
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
