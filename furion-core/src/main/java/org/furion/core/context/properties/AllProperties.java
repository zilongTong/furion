package org.furion.core.context.properties;

import lombok.Data;

import java.util.Properties;

/**
 * 以Properties 为原型，承载所有 配置项的值。
 * 分为：本地配置文件、系统环境变量。
 * <p>
 * final类型
 */
@Data
public final class AllProperties {

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
     * 取值。按照优先级，遍历读取，优先级原则：
     * 如：网络配置中心>环境变量>配置文件
     */
    public <T> T getPropertyValueByKey(String key, Class<T> tClass) {

        return null;
    }

}
