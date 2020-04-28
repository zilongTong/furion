package org.furion.core.filter.load;

import org.apache.commons.collections.CollectionUtils;
import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.filter.FurionFilter;
import org.furion.core.utils.ClassUtil;
import java.util.List;
import java.util.Set;

/**
 * 负责从 当前Java 项目中 加载 所有Filter
 */
public class PackageFilterLoader extends BaseFilterLoader {

    @Override
    public void loadFilter(FurionGatewayContext context) {
        String contextPackageName = FurionGatewayContext.class.getPackage().getName();

        Set<Class<?>> classes = ClassUtil.getClasses(contextPackageName);
        if (CollectionUtils.isNotEmpty(classes)) {
            classes.forEach(item -> {
                if (item.isAssignableFrom(FurionFilter.class)) {
                    try {
                        Object o = item.newInstance();
                        FurionFilter filter = (FurionFilter) o;
                        context.addFilter(filter);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    List<ClassResourceContainer> getResource(FurionGatewayContext context) {
        throw new RuntimeException("错误调用");
    }

}
