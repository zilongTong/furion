package org.furion.core.filter.load;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.filter.FurionFilter;
import org.furion.core.utils.ClassLoaderUtil;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载Filter Class 基类。
 * 模板方法模式：获取InputStream——》加载获取Class——》实例化对象——》保存到context
 * 子类需要实现 InputStream 获取逻辑。
 */
public abstract class BaseFilterLoader implements IFilterLoader {

    private static ConcurrentHashMap<String, Date> loadedClass = new ConcurrentHashMap<>();


    @Override
    public void loadFilter(FurionGatewayContext context) {
        //多个class资源
        List<ClassResourceContainer> resource = getResource(context);
        if (CollectionUtils.isEmpty(resource)) {
            return;
        }
        resource.forEach(io -> {
            loadFilter(context, io);
        });
    }

    @Override
    public void loadFilter(FurionGatewayContext context, ClassResourceContainer classResourceContainer) {
        if (needToLoad(classResourceContainer)) {
            try {
                Class aClass = loadClass(classResourceContainer.getByteArrayInputStream());
                FurionFilter instanceFromClass = getInstanceFromClass(aClass);
                context.addFilter(instanceFromClass);
            } catch (Exception e) {
                e.printStackTrace();
                removeLoadRecord(classResourceContainer);
            }
        }
    }

    /**
     * 判断此资源 是否需要加载。
     */
    synchronized boolean needToLoad(ClassResourceContainer container) {
        String classResourceId = container.getClassResourceId();
        Date date = loadedClass.get(classResourceId);

        if (date == null || container.getLastUpdateTime().getTime() > date.getTime()) {
            loadedClass.put(classResourceId, container.getLastUpdateTime());
            return true;
        }
        return false;
    }

    synchronized void removeLoadRecord(ClassResourceContainer container) {
        loadedClass.remove(container.getClassResourceId());
    }

    synchronized void finishLoad(ClassResourceContainer container) {
        synchronized (loadedClass) {
            if (!"LOADING".equals(loadedClass.get(container.getClassResourceId()))) {
                throw new RuntimeException("Filter 加载 错误,线程不安全");
            }

        }
    }


    /**
     * 不同子类实现 获取class inputStream 逻辑
     */
    abstract List<ClassResourceContainer> getResource(FurionGatewayContext context);

    protected Class loadClass(ByteArrayInputStream inputStream) {
        return ClassLoaderUtil.loadClass(inputStream);
    }


    protected FurionFilter getInstanceFromClass(Class aClass) {
        try {
            if (aClass.isAssignableFrom(FurionFilter.class)) {
                Class<FurionFilter> furionFilterClass = aClass;
                return furionFilterClass.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
