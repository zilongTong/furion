package org.furion.core.context.properties;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.furion.core.enumeration.PropertiesType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wplin
 * 2020年04月23日11:36:15
 * 单例模式
 */
@Slf4j
public final class PropertiesManager implements IPropertiesManager {

    private static PropertiesManager propertiesManager;
    private static final ConcurrentHashMap<Class, IPropertiesContainer> containerMap = new ConcurrentHashMap<>();

    /**
     * 本地配置文件
     */
    private Properties localProperties;
    /**
     * 系统个环境变量，包括启动参数
     */
    private Properties systemProperties;
    /**
     * 网络配置中心变量，如：Apollo，admin、zookeeper等
     */
    private Properties netProperties;

    /**
     * 单例实现 后期再优化
     */
    public static PropertiesManager getInstance() {
        if (propertiesManager == null) {
            propertiesManager = new PropertiesManager();
            propertiesManager.init();
        }
        return propertiesManager;
    }

    private PropertiesManager() {
        localProperties = new Properties();
        systemProperties = new Properties();
        netProperties = new Properties();
    }

    private void init() {
        loadPropsFromSystem();
        loadPropsFromLocalFile(PropertiesManager.class.getResource("").getPath());
    }

    /**
     * 加载系统 环境变量
     */
    private void loadPropsFromSystem() {
        systemProperties = (Properties) System.getProperties().clone();
    }

    /**
     * 加载本地配置文件变量
     *
     * @param path
     */
    private void loadPropsFromLocalFile(String path) {
        localProperties = new Properties();
        final File propsFile = new File(path);
        if (propsFile.isFile()) {
            try (InputStream is = new FileInputStream(propsFile)) {
                localProperties.load(is);
            } catch (final IOException e) {
                log.warn("Could not load props file?", e);
            }
        }
    }


    @Override
    public void register(IPropertiesContainer container) {
        containerMap.put(container.getClass(), container);
    }

    @Override
    public <T> T getPropertiesContainer(Class<? extends IPropertiesContainer> c) {
        IPropertiesContainer container = containerMap.get(c);
        if (container == null) {
            return null;
        }
        return (T) container;
    }

    /**
     * Properties更新
     *
     * @param properties
     */
    @Override
    public void refresh(PropertiesType propertiesType, Properties properties) {

        //TODO 更新对应的properties
        switch (propertiesType) {
            //本地
            case LOCAL:
                handlePropertiesRefresh(localProperties, properties);
                break;
            case NET:
                handlePropertiesRefresh(netProperties, properties);
                break;
            case SYSTEM:
                handlePropertiesRefresh(systemProperties, properties);
                break;
            default:

        }
    }

    private void handlePropertiesRefresh(Properties old, Properties n) {

        /*
            比对key-value ,计算出更新项、事件。
         */
        List<PropertyValueChangeEvent> list = Lists.newArrayList();

        /*
            循环通知所有 PropertiesContainer进行更新
         */
        containerMap.forEach((k, v) -> {
            v.refresh(list);
        });
    }


    /**
     * 取值。按照优先级，遍历读取，优先级原则：
     * 如：网络配置中心>环境变量>配置文件。
     */
    @Override
    public <V> V getPropertyValue(String key, Class<V> tClass) {
        String value = localProperties.getProperty(key);

        if (systemProperties.containsKey(key)) {
            value = systemProperties.getProperty(key);
        }
        if (netProperties.containsKey(key)) {
            value = netProperties.getProperty(key);
        }
        //TODO 类型转换
        return (V) value;
    }


}
