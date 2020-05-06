package org.furion.core.context.properties.convert;

import com.google.common.collect.Sets;

import java.util.Set;

public class StringConverter implements TypeConvert {
    private Set<Class> sets = Sets.newHashSet(String.class);

    @Override
    public boolean iCanDoIt(Class c) {
        return sets.contains(c);
    }

    @Override
    public Object doIt(String value) {
        return value;
    }
}
