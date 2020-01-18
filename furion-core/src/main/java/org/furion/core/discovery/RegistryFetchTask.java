package org.furion.core.discovery;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Functional description
 * 获取eureka信息
 *
 * @author Leo
 * @date 2019-12-19
 */
@Slf4j
public class RegistryFetchTask {

    private Thread fetchRegisterThread;

    private static RegistryFetchTask instance = new RegistryFetchTask();

    private RegistryFetchTask() {
    }

    public static RegistryFetchTask getInstance() {
        return instance;
    }

    private volatile boolean isStop = false;

    public void execute(final Long timeGap) {
        fetchRegisterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    try {
                        EurekaNetWork.fetchAllService();
                    } catch (Exception e) {
                        log.error("fetchRegisterThread fetch eureka application error", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(timeGap);
                    } catch (InterruptedException e) {
                        log.info("fetchRegisterThread InterruptedException", e);
                    }
                }
                //如果线程中断，把缓存清空，让其他线程自己去拿数据
                EurekaNetWork.applicationCache.setServiceBeans(null);
            }
        });
        fetchRegisterThread.setDaemon(true);
        fetchRegisterThread.setName("fetchRegisterThread, fetch eureka application per 5s");
        fetchRegisterThread.start();
    }

    public void stop() {
        isStop = true;
        fetchRegisterThread.interrupt();
        try {
            fetchRegisterThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
