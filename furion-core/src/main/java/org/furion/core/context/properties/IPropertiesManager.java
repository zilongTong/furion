package org.furion.core.context.properties;

/**
 * 所有配置项管理 顶级类。负责：
 * 1、管理 IPropertiesContainer
 * 2、加载本地、系统、网络配置量
 * 3、接收外部 配置项变动事件
 * 自身也可作为 IPropertiesContainer
 */
public interface IPropertiesManager extends IPropertiesContainer {

    void register(IPropertiesContainer container);

    <T> T getProperties(Class<? extends IPropertiesContainer> c);


}
