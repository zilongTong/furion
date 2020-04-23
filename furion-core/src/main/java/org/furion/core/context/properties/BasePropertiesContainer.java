package org.furion.core.context.properties;

public abstract class BasePropertiesContainer implements IPropertiesContainer {


    @Override
    public void register(PropertiesManager propertiesRepository) {
        propertiesRepository.register(this);
    }
}
