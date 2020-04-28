package org.furion.core.context;

import org.furion.core.filter.FilterType;
import org.furion.core.filter.FurionFilter;
import org.furion.core.filter.FurionFilterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

public class FurionGatewayContext implements GatewayContext {

    private static final Logger LOG = LoggerFactory.getLogger(FurionGatewayContext.class);

    private FurionProperties FurionProperties;

    private static FurionFilterRegistry registry;


    public static FurionFilterRegistry getRegistry() {
        return registry;
    }

    public static void setRegistry(FurionFilterRegistry registry) {
        FurionGatewayContext.registry = registry;
    }

    public FurionGatewayContext() {
        refresh();
    }

    public static void main(String[] args) {
        System.out.println(FurionGatewayContext.class.getResource("/META-INF").getPath());
    }

    /**
     * 加载Filter
     */
    public void refresh() {
        //
    }


//    @Override
//    public Set<FurionFilter> getFilterSetByType(FilterType filterType) {
//        if (FilterType.POST == filterType) {
//            return postFilters;
//        } else if (FilterType.PRE == filterType) {
//            return preFilters;
//        }
//        throw new RuntimeException("invalid filterType:" + filterType);
//
//    }

    @Override
    public void addFilter(FurionFilter filter) {
        registry.registerFilter(filter);
    }
}
