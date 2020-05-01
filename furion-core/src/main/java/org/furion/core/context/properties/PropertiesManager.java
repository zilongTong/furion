package org.furion.core.context.properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.furion.core.annotation.PropertiesObject;
import org.furion.core.context.properties.convert.PrimitiveConverter;
import org.furion.core.context.properties.convert.StringConverter;
import org.furion.core.context.properties.convert.TypeConvert;
import org.furion.core.enumeration.PropertiesSource;
import org.furion.core.enumeration.PropertyValueChangeType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
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
     * init()方法后，将所有的Properties 按照优先级读取所有K-v 构建Add事件，当有IPropertiesContainer注册时，立即执行refresh()
     */
    private static List<IPropertyValueChangeEvent> allInitValue = Lists.newArrayList();

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

    private static Set<TypeConvert> converter = Sets.newHashSet(new StringConverter(), new PrimitiveConverter());

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
        //加载。保持顺序
        loadPropsFromSystem();
        loadPropsFromLocalFile();
        //TODO 如果有类似 apollo的配置平台，则需先加载网络配置文件后，再执行后续操作


        //获取并集：后面覆盖前面
        Properties all = new Properties();
        all.putAll(localProperties);
        all.putAll(systemProperties);
        all.putAll(netProperties);
        all.forEach((k, v) -> {
            if (k == null || v == null) {
                return;
            }
            allInitValue.add(new SimplePropertyValueChangeEvent.SimplePropertyValueChangeEventBuilder()
                    .key(k.toString())
                    .eventType(PropertyValueChangeType.ADD)
                    .newValue(v.toString()).build());
        });
    }

    /**
     * 加载系统 环境变量
     */
    private void loadPropsFromSystem() {
        systemProperties = (Properties) System.getProperties().clone();
    }

    /**
     * 加载本地配置文件变量：
     * TODO 默认路径定义
     * 后期可优化为多文件、多环境
     */
    private void loadPropsFromLocalFile() {
        String path = getLocalPropertiesFilePath();
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

    private String getLocalPropertiesFilePath() {
        //默认路径
        //获取环境变量动态设置值
        String path = PropertiesManager.class.getResource("").getPath();
        String pathFromSystem = getPropertyValue("config-path", String.class);
        if (StringUtils.isNotBlank(pathFromSystem)) {
            path = pathFromSystem;
        }
        return path;
    }

    /**
     * IPropertiesContainer 注册到Manager,立即推送现有的Properties值到Container.
     * 保证在IPropertiesContainer 实例化后已经填充值。
     */
    @Override
    public void register(IPropertiesContainer container) {
        containerMap.put(container.getClass(), container);
        initPropertiesObject(container);
        container.refresh(allInitValue);
    }

    /**
     * 利用反射，直接设置properties对象属性值
     *
     * @param container
     */
    private void initPropertiesObject(IPropertiesContainer container) {
        Class<? extends IPropertiesContainer> aClass = container.getClass();
        PropertiesObject annotation = aClass.getAnnotation(PropertiesObject.class);
        String prefix = "";
        if (annotation != null) {
            prefix = annotation.prefix();
        }
        Field[] fields = aClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            setFieldValue(container, prefix, field);
        }
    }


    private void setFieldValue(IPropertiesContainer container, String prefix, Field field) {
        String key = prefix + field.getName();
        Class<?> type = field.getType();

        Object propertyValue = getPropertyValue(key, type);
        if (propertyValue == null) {
            return;
        }
        try {
            field.set(container, propertyValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
     * Properties 接收外部更新
     */
    @Override
    public synchronized void refresh(PropertiesSource propertiesSource, Properties properties) {

        switch (propertiesSource) {
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
        if (n == null) {
            return;
        }
        /*
            比对key-value ,计算出更新项、事件。
         */
        List<IPropertyValueChangeEvent> list = Lists.newArrayList();

        n.forEach((k, v) -> {
            if (k == null || v == null) {
                return;
            }
            String key = (String) k;
            String oldValue = old.getProperty(key);
            String newValue = v.toString();
            //新增key
            //兼容value = "" 情况
            if (oldValue == null) {
                list.add(new SimplePropertyValueChangeEvent.SimplePropertyValueChangeEventBuilder()
                        .key(key)
                        .eventType(PropertyValueChangeType.ADD)
                        .newValue(newValue).build());
            } else if (!oldValue.equals(newValue)) {
                //更新
                list.add(new SimplePropertyValueChangeEvent.SimplePropertyValueChangeEventBuilder()
                        .key(key)
                        .eventType(PropertyValueChangeType.UPDATE)
                        .oldValue(oldValue)
                        .newValue(newValue).build());
            }
        });
        //删除，反向循环比对
        Set<String> deleteKey = Sets.newHashSet();

        old.forEach((k, v) -> {
            if (k == null || v == null) {
                return;
            }
            String key = k.toString();
            if (!n.containsKey(k)) {
                list.add(new SimplePropertyValueChangeEvent.SimplePropertyValueChangeEventBuilder()
                        .key(key)
                        .eventType(PropertyValueChangeType.DELETE)
                        .oldValue(old.getProperty(key))
                        .build());

                deleteKey.add(key);
            }
        });


        /*
            循环通知所有 PropertiesContainer进行更新
         */
        containerMap.forEach((k, v) -> {
            v.refresh(list);
        });

        //更新原Properties
        old.putAll(n);
        //删除key
        deleteKey.forEach(old::remove);

    }


    /**
     * 取值。按照优先级，遍历读取，优先级原则：
     * 如：网络配置中心>环境变量>配置文件。
     * 支持：
     * 1、单值，如String、基础类型
     * 2、List<String> 类型
     * 3、Map<String,Object> key
     */
    @Override
    public <V> V getPropertyValue(String key, Class<V> tClass) {
        //单值 类型
        if (singleValue(tClass)) {
            String value = getStringValue(key);
            if (StringUtils.isBlank(value)) {
                return null;
            }

            if (typeCheck(tClass, Double.class, double.class)) {
                return (V) Double.valueOf(value);
            } else if (typeCheck(tClass, Long.class, long.class)) {
                return (V) Long.valueOf(value);
            } else if (typeCheck(tClass, int.class, Integer.class, char.class)) {
                return (V) Integer.valueOf(value);
            } else if (typeCheck(tClass, boolean.class, Boolean.class)) {
                return (V) Boolean.valueOf(value);
            } else if (typeCheck(tClass, String.class)) {
                return (V) value;
            } else {
                System.out.println("不支持类型 " + tClass.getSimpleName());
                return null;
            }
        } else
            //List。设定以key[0] key[0] 方式填充值
            if (tClass == List.class) {
                Map<String, String> vs = getListValue(key + "[", "]");
                if (vs.isEmpty()) {
                    return (V) Lists.newArrayList();
                }
                String[] arr = getMaxIndex(vs);
                List<String> listValue = Lists.newArrayList(arr);
                return (V) listValue;
            } else if (tClass == Map.class) {
                Map<String, String> vs = getListValue(key + ".", null);
                // TODO
                return null;
            }

        throw new RuntimeException("暂不支持此种类型");
    }


    private String[] getMaxIndex(Map<String, String> vs) {
        String[] arr = new String[0];
        Iterator<Map.Entry<String, String>> iterator = vs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String k = entry.getKey();
            String value = entry.getValue();
            String num = k.substring(k.lastIndexOf("[") + 1, k.lastIndexOf("]"));
            if (StringUtils.isBlank(num)) {
                continue;
            }
            Integer integer = Integer.valueOf(num);
            if (integer < 0) {
                continue;
            }
            //数组扩容
            if (integer + 1 > arr.length) {
                arr = Arrays.copyOf(arr, integer + 1);
            }
            arr[integer] = value;

        }
        return arr;
    }

    /**
     * 单值类型：String，基础类型
     */
    private boolean singleValue(Class type) {
        return type.isPrimitive() ||
                type == String.class;
    }

    private boolean typeCheck(Class type, Class... target) {
        for (Class item : target) {
            if (item == type) {
                return true;
            }
        }
        return false;
    }

    private String getStringValue(String key) {
        String value = localProperties.getProperty(key);

        if (systemProperties.containsKey(key)) {
            value = systemProperties.getProperty(key);
        }
        if (netProperties.containsKey(key)) {
            value = netProperties.getProperty(key);
        }
        return value;
    }


    /**
     * 获取以某种key开头\结尾的所有 配置项
     */
    private Map<String, String> getListValue(String keyPrefix, String keySuffix) {
        Map<String, String> map = Maps.newHashMap();
        doGetListValue(keyPrefix, keySuffix, map, localProperties);
        doGetListValue(keyPrefix, keySuffix, map, systemProperties);
        doGetListValue(keyPrefix, keySuffix, map, netProperties);
        return map;
    }

    private void doGetListValue(String keyPrefix, String keySuffix, Map<String, String> map, Properties properties) {

        properties.forEach((k, v) -> {
            if (StringUtils.isBlank(keyPrefix) && StringUtils.isNotBlank(keySuffix)) {
                if (k.toString().endsWith(keySuffix)) {
                    map.put(k.toString(), v.toString());
                }
            } else if (StringUtils.isBlank(keySuffix) && StringUtils.isNotBlank(keyPrefix)) {
                if (k.toString().startsWith(keyPrefix)) {
                    map.put(k.toString(), v.toString());
                }
            } else if (StringUtils.isNotBlank(keySuffix) && StringUtils.isNotBlank(keyPrefix)) {
                if (k.toString().startsWith(keyPrefix) && k.toString().endsWith(keySuffix)) {
                    map.put(k.toString(), v.toString());
                }
            } else {
                throw new RuntimeException("过滤条件不能全为空");
            }

        });
    }

}
