package org.furion.core.filter;

import com.sun.tools.javac.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.context.FurionGatewayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DynamicFilterRegisterTask {

    private static final Logger log = LoggerFactory.getLogger(DynamicFilterRegisterTask.class);

    private Thread filterScanThread;

    private static DynamicFilterRegisterTask instance = new DynamicFilterRegisterTask();

    private volatile boolean isStop = false;


    /**
     * 默认扫描间隔 30s
     */
    private long timeGap = 30 * 1000;
    private String filterFilePath = "/opt/data/filters";
    private long minTimeGap = 10 * 1000;


    private DynamicFilterRegisterTask() {
    }

    public static DynamicFilterRegisterTask getInstance() {
        return instance;
    }


    private void execute(final Long timeGap) {
        filterScanThread.start();
    }

    public void start(Long timeGap, String path) {
        if (timeGap != null && timeGap > minTimeGap) {
            this.timeGap = timeGap;
        }

        if (StringUtils.isNotBlank(path)) {
            filterFilePath = path;
        }
        prepareTheadTask();
        filterScanThread.start();


    }

    /**
     * 准备线程
     */
    private void prepareTheadTask() {
        filterScanThread = new Thread("FilterFileManagerPoller") {
            @Override
            public void run() {
                while (!isStop || !filterScanThread.isInterrupted()) {
                    try {
                        /*
                        扫描指定目录下的Filter Java 源文件，交由FilterManager 统一处理
                         */
                        FilterManager.getInstance().acceptFilterFile(null);

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
        };
        filterScanThread.setDaemon(true);
        filterScanThread.setName("filterScanThread, scan filter  per 30s");
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
