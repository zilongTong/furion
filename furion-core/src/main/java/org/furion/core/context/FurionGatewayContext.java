package org.furion.core.context;

import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.filter.FilterManager;
import org.furion.core.filter.FilterType;
import org.furion.core.filter.FurionFilter;
import org.furion.core.filter.FurionFilterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对外部用户的统一入口。
 */
public class FurionGatewayContext implements GatewayContext {

    private static final Logger LOG = LoggerFactory.getLogger(FurionGatewayContext.class);
    private static FurionGatewayContext INSTANCE;
    private PropertiesManager propertiesManager;
    private FurionProperties furionProperties;
    private FilterManager filterManager;
    private FurionFilterRegistry registry;
    private Class PROJECT_MAIN_CLASS;


    public FurionFilterRegistry getRegistry() {
        return registry;
    }

    public Class mainClass() {
        return getInstance().PROJECT_MAIN_CLASS;
    }

    public static FurionGatewayContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FurionGatewayContext(null);
        }
        return INSTANCE;
    }

    public FurionGatewayContext(Class mainClass) {
        INSTANCE = this;
        if (mainClass == null) {
            mainClass = FurionGatewayContext.class;
        }
        PROJECT_MAIN_CLASS = mainClass;
        propertiesManager = PropertiesManager.getInstance();
        filterManager = FilterManager.getInstance();
        registry = FurionFilterRegistry.getInstance();
        furionProperties = new FurionProperties();
        init();
    }

    public static void main(String[] args) {
        System.out.println(FurionGatewayContext.class.getResource("/META-INF").getPath());
    }

    private void init() {
        propertiesManager.init();
        filterManager.init();
    }

    public void start() {

    }


    @Override
    public void addFilter(FurionFilter filter) {
        registry.registerFilter(filter);
    }
}
