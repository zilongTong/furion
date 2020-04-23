package org.furion.core.context.properties;

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
public interface IPropertiesManager extends IPropertiesContainer {

    void register(IPropertiesContainer container);

    /**
     * 按照Class获取具体的Properties类。
     */
    <T> T getProperties(Class<? extends IPropertiesContainer> c);


}
