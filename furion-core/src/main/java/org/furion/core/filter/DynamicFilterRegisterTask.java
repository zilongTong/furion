package org.furion.core.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sun.tools.javac.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DynamicFilterRegisterTask {

    private static final Logger log = LoggerFactory.getLogger(DynamicFilterRegisterTask.class);

    private Thread filterScanThread;

    private volatile boolean isStop = false;

    private String scanPath;
    private long timeGap;

    public DynamicFilterRegisterTask(String scanPath, long timeGap) {
        this.scanPath = scanPath;
        this.timeGap = timeGap;
    }

    private void execute(final Long timeGap) {
        filterScanThread.start();
    }

    public void start() {
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

                        Set<File> set = FileUtil.listFileFromDir(scanPath, new FilenameFilter() {
                            @Override
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".java");
                            }
                        });

                        FilterManager.getInstance().acceptFilterFile(set);

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

    private void listFile(File file, Set<File> set) {
        if (file.isFile() && isJavaCodeFile(file)) {
            set.add(file);
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File item : files) {
                if (isJavaCodeFile(item)) {
                    listFile(item, set);
                }
            }
        }
    }

    private boolean isJavaCodeFile(File file) {
        return file != null && file.getName().endsWith(".java");
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
