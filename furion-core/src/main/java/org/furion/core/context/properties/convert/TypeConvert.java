package org.furion.core.context.properties.convert;

public interface TypeConvert {
    boolean iCanDoIt(Class c);

    Object doIt(String key);

}
