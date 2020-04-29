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




    public <T> T getPropertyValueByKey(String key, Class<T> tClass) {

        return null;
    }

}
