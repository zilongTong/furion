package org.furion.core.filter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.utils.ClassUtil;
import org.furion.core.utils.CompileUtil;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JavaCompiler 编译器  只支持对文件编译，不支持文件流等其他方式。
 * Filter 无论以何种方式传递到网关服务器，最终都必须落地到文件，因此此类取名FilterFileManager。
 * 单例、不可覆盖。
 * 实例化当前虚拟机中已存在的Filter(包括网关自定义、用户工程中自定义的),并加载到 FilterRegistry。init()中只调用一次。
 * 启动轮询本地文件线程，判断为 新增或更新，则完成后续加载处理 动态加载本地Filter文件
 * 外部Filter保存到本地后，被扫描加载。
 */
public final class FilterManager {

    private static FilterManager INSTANCE;
    private boolean init = false;

    private String filterFilePath = "/opt/data/filters";
    /**
     * 默认扫描间隔 30s
     */
    private long timeGap = 30;
    private long minTimeGap = 10;

    private FurionFilterRegistry filterRegistry;
    private ConcurrentHashMap<String, Long> fileLastModify;

    private FilterManager() {
        filterRegistry = FurionFilterRegistry.getInstance();
        fileLastModify = new ConcurrentHashMap<>();
        String path = PropertiesManager.getInstance().getSinglePropertyValue("filter.path", String.class);
        if (StringUtils.isNotBlank(path)) {
            filterFilePath = path;
        }
    }

    public static FilterManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FilterManager();
        }
        return INSTANCE;
    }

    public void init() {
        if (init) {
            return;
        }
        String filePath = PropertiesManager.getInstance().getSinglePropertyValue("filter.filePath", String.class);
        if (StringUtils.isNotBlank(filePath)) {
            filterFilePath = filePath;
        }

        Long filterTimeGap = PropertiesManager.getInstance().getSinglePropertyValue("filter.timeGap", Long.class);
        if (filterTimeGap != null && filterTimeGap >= minTimeGap) {
            timeGap = filterTimeGap;
        }
        loadPackageFilter();
        loadLocalFileFilter();
    }

    /**
     * 接收
     *
     * @param files
     */
    public void acceptFilterFile(Set<File> files) {
        if (files == null) {
            return;
        }
        files.forEach(item -> {
            String fileAbsolutePath = item.getAbsolutePath();
            Long fileLastModifyTime = item.lastModified();
            Long aLong = fileLastModify.get(fileAbsolutePath);
            if (aLong == null || fileLastModifyTime > aLong) {
                fileLastModify.put(fileAbsolutePath, fileLastModifyTime);
                handleFile(item);
            }
        });

    }

    private void handleFile(File file) {
        if (file == null) {
            return;
        }
        String fileAbsolutePath = file.getAbsolutePath();
        Set<File> files = CompileUtil.compileJavaFileToClassFiles(file, filterFilePath);
        LClassLoader lClassLoader = new LClassLoader(files);
        Set<Class<?>> classes = lClassLoader.loadClasses();
        if (classes != null) {
            for (Class filter : classes) {
                /*
                    只实例化一个FurionFilter实例。
                    内部类已加载。
                 */
                if (filter == null) {
                    continue;
                }
                if (FurionFilter.class.isAssignableFrom(filter)) {
                    try {
                        Object o = filter.newInstance();
                        filterRegistry.registerFilter(filter.getName(), (FurionFilter) o);
                        return;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }


    private void loadLocalFileFilter() {
        new DynamicFilterRegisterTask(filterFilePath, timeGap).start();
    }

    private void loadPackageFilter() {
        String contextPackageName = FurionGatewayContext.getInstance().mainClass().getPackage().getName();

        Set<Class<?>> classes = ClassUtil.getClasses(contextPackageName);
        if (CollectionUtils.isNotEmpty(classes)) {
            classes.forEach(item -> {
                if (FurionFilter.class.isAssignableFrom(item)) {
                    try {
                        Object o = item.newInstance();
                        FurionFilter filter = (FurionFilter) o;
                        filterRegistry.registerFilter(filter.getClass().getName(), filter);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
