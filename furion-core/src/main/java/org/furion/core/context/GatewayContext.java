package org.furion.core.context;

import org.furion.core.filter.FilterType;
import org.furion.core.filter.FurionFilter;

import java.util.Set;

public interface GatewayContext {


    Set<FurionFilter> getFilterSetByType(FilterType filterType);

    void addFilter(FurionFilter filter);

    /**
     * 本地配置文件读取properties 后存于Context中。
     * 从context获取各种配置项的值
     */
    <T> T getPropertyValueByKey(String key, Class<T> tClass);


}
