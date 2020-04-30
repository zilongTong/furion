package org.furion.core.context.properties;

import lombok.Builder;
import org.furion.core.enumeration.PropertyValueChangeType;

@Builder
public class SimplePropertyValueChangeEvent implements IPropertyValueChangeEvent {

    private String key;
    private PropertyValueChangeType eventType;
    private String oldValue;
    private String newValue;

    @Override
    public String key() {
        return key;
    }

    @Override
    public PropertyValueChangeType eventType() {
        return eventType;
    }

    @Override
    public String oldValue() {
        return oldValue;
    }

    @Override
    public String newValue() {
        return newValue;
    }
}
