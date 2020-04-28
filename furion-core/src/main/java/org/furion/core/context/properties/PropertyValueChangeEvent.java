package org.furion.core.context.properties;

import org.furion.core.enumeration.PropertyValueChangeType;

/**
 * 配置项变更 封装数据
 */
public interface PropertyValueChangeEvent {


    String key();

    PropertyValueChangeType eventType();

    Object oldValue();

    Object newValue();
}
