package org.furion.core.filter;

import org.apache.commons.collections.CollectionUtils;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.utils.ClassUtil;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JavaCompiler 编译器  只支持对文件编译，不支持文件流等其他方式。
 * Filter 无论以何种方式传递到网关服务器，最终都必须落地到文件，因此此类取名FilterFileManager。
 * 单例、不可覆盖。
 * 实例化当前虚拟机中已存在的Filter(包括网关自定义、用户工程中自定义的),并加载到 FilterRegistry。init()中只调用一次。
 * 启动轮询本地文件线程，判断为 新增或更新，则完成后续加载处理 动态加载本地Filter文件
 * 提供外部传入Java文件流接口
 */
public final class FilterManager {

    private static FilterManager INSTANCE;
    private boolean init = false;

    private FurionFilterRegistry filterRegistry = FurionGatewayContext.getRegistry();

    private FilterManager() {

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
        loadPackageFilter();
        loadLocalFileFilter();
    }

    /**
     * 接收
     * @param files
     */
    public void acceptFilterFile(List<File> files) {

    }


    private void loadLocalFileFilter() {

        String filterPath = PropertiesManager.getInstance().getSinglePropertyValue("filterPath", String.class);
        Long filterTimeGap = PropertiesManager.getInstance().getSinglePropertyValue("filterTimeGap", Long.class);
        DynamicFilterRegisterTask.getInstance().start(filterTimeGap, filterPath);
    }

    private void loadPackageFilter() {
        String contextPackageName = FurionGatewayContext.class.getPackage().getName();

        Set<Class<?>> classes = ClassUtil.getClasses(contextPackageName);
        if (CollectionUtils.isNotEmpty(classes)) {
            classes.forEach(item -> {
                if (item.isAssignableFrom(FurionFilter.class)) {
                    try {
                        Object o = item.newInstance();
                        FurionFilter filter = (FurionFilter) o;
                        filterRegistry.registerFilter(filter);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
