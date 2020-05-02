package org.furion.core.context.properties;

import org.furion.core.enumeration.PropertiesSource;

import java.util.Collection;
import java.util.Properties;

/**
 * 所有配置项管理 顶级类。负责：
 * 1、管理 IPropertiesContainer
 * 2、加载本地、系统、网络配置量
 * 3、接收外部 配置项变动事件
 * 自身也可作为 IPropertiesContainer
 *
 * @author wplin
 * 2020年04月23日11:36:36
 */
public interface IPropertiesManager {

    /**
     * 接收新的Properties值：网络推送、本地扫描 等
     */
    void refresh(PropertiesSource propertiesSource, Properties properties);

    /**
     * 获取单值,如String、基础数据类型、包装类型
     */
    <V> V getSinglePropertyValue(String key, Class<V> tClass);

    /**
     * 获取String集合
     */
    Collection<String> getCollectionPropertyValue(String key, Class<? extends Collection> tClass);


    void register(IPropertiesContainer container);


}
