package org.furion.core.context.properties;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wplin
 * 2020年04月23日11:36:15
 */
@Slf4j
public final class PropertiesManager implements IPropertiesManager {
    /**
     * 单例模式
     */
    private static final PropertiesManager propertiesManager = new PropertiesManager();
    private static final ConcurrentHashMap<Class, IPropertiesContainer> containerMap = new ConcurrentHashMap<>();

    /**
     * 承载所有原始配置项
     */
    private static final AllProperties allProperties = new AllProperties();


    public static PropertiesManager getInstance() {
        return propertiesManager;
    }

    private PropertiesManager() {
        loadPropsFromSystem();
        loadPropsFromLocalFile(PropertiesManager.class.getResource("").getPath());
    }

    /**
     * 加载环境变量
     */
    private void loadPropsFromSystem() {

    }

    /**
     * 加载本地配置文件变量
     *
     * @param path
     */
    private void loadPropsFromLocalFile(String path) {
        Properties properties = new Properties();
        final File propsFile = new File(path);
        if (propsFile.isFile()) {
            try (InputStream is = new FileInputStream(propsFile)) {
                properties.load(is);
                allProperties.setLocalProperties(properties);
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
    public <T> T getProperties(Class<? extends IPropertiesContainer> c) {
        IPropertiesContainer container = containerMap.get(c);
        if (container == null) {
            return null;
        }
        return (T) container;
    }

    @Override
    public <V> V getPropertyValue(String key, Class<V> tClass) {
        return allProperties.getPropertyValueByKey(key, tClass);
    }

    @Override
    public void register(PropertiesManager propertiesRepository) {
        propertiesRepository.register(this);
    }
}
