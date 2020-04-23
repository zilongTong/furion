package org.furion.core.context;

import org.furion.core.filter.FilterType;
import org.furion.core.filter.FurionFilter;

import java.util.Set;

public interface GatewayContext {


    Set<FurionFilter> getFilterSetByType(FilterType filterType);

    void addFilter(FurionFilter filter);




}
