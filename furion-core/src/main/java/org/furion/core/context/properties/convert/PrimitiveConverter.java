package org.furion.core.context.properties.convert;

public class PrimitiveConverter implements TypeConvert {
    @Override
    public boolean iCanDoIt(Class c) {
        return c.isPrimitive();
    }

    @Override
    public Object doIt(String value) {
        return value;
    }
}
