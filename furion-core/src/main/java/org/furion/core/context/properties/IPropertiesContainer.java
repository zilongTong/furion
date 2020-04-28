package org.furion.core.context.properties;

import java.util.List;

/**
 * 配置对象 容器规范
 */
public interface IPropertiesContainer {
    /**
     * 初始化所有配置项
     */
    default void init(AllProperties allProperties) {
    }

    /**
     * 接收 具体的 配置项更新事件，更新自身。
     */
    default void refresh(List<PropertyValueChangeEvent> refreshDataList) {
    }

    void register(PropertiesManager propertiesManager);

    <V> V getPropertyValue(String key, Class<V> tClass);


}
