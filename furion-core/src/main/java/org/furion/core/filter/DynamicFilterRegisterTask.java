package org.furion.core.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DynamicFilterRegisterTask {

    private static final Logger log = LoggerFactory.getLogger(DynamicFilterRegisterTask.class);

    private Thread filterScanThread;

    private static DynamicFilterRegisterTask instance = new DynamicFilterRegisterTask();

    private DynamicFilterRegisterTask() {
    }

    public static DynamicFilterRegisterTask getInstance() {
        return instance;
    }

    private volatile boolean isStop = false;

    public void execute(final Long timeGap) {
        filterScanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isStop) {
                    try {

                    } catch (Exception e) {
                        log.error("filterScanThread  error", e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(timeGap);
                    } catch (InterruptedException e) {
                        log.info("filterScanThread InterruptedException", e);
                    }
                }


            }
        });
        filterScanThread.setDaemon(true);
        filterScanThread.setName("filterScanThread, scan filter  per 30s");
        filterScanThread.start();
    }

    public void stop() {
        isStop = true;
        filterScanThread.interrupt();
        try {
            filterScanThread.join();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
