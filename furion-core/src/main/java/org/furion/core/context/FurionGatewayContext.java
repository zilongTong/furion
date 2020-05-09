package org.furion.core.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.filter.FilterManager;

import org.furion.core.filter.FurionFilter;
import org.furion.core.filter.FurionFilterRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 对外部用户的统一入口。
 */
@Data
public class FurionGatewayContext implements GatewayContext {

    private static final Logger LOG = LoggerFactory.getLogger(FurionGatewayContext.class);
    private static FurionGatewayContext INSTANCE;
    private PropertiesManager propertiesManager;
    private FurionProperties furionProperties;
    private SystemProperties systemProperties;
    private FilterManager filterManager;
    private FurionFilterRegistry registry;
    /**
     * 用户启动程序所在Main class.
     * 扫描此class package下的 Filter 加载。
     */
    private Class PROJECT_MAIN_CLASS;


    public FurionFilterRegistry getRegistry() {
        if (registry == null) {
            registry = new FurionFilterRegistry();
        }
        return registry;
    }


//    public FurionFilterRegistry getRegistry() {
//        return registry;
//    }

    public Class mainClass() {
        return getInstance().PROJECT_MAIN_CLASS;
    }

    public static FurionGatewayContext getInstance() {
        if (INSTANCE == null) {
            System.setProperty("config-path",FurionGatewayContext.class.getResource("/config").getPath());
            INSTANCE = new FurionGatewayContext((Class) null);
        }
        return INSTANCE;
    }

    public FurionGatewayContext(String... args) {

    }

    public FurionGatewayContext(Class mainClass) {
        INSTANCE = this;
        if (mainClass == null) {
            mainClass = FurionGatewayContext.class;
        }
        PROJECT_MAIN_CLASS = mainClass;
        propertiesManager = PropertiesManager.getInstance();
        propertiesManager.init();
        furionProperties = new FurionProperties();
        systemProperties = new SystemProperties();
        filterManager = FilterManager.getInstance();
        registry = new FurionFilterRegistry();
        furionProperties = new FurionProperties();
        init();
    }

    public static void main(String[] args) throws Exception{
        System.setProperty("config-path",FurionGatewayContext.class.getResource("/config").getPath());
        FurionGatewayContext furionGatewayContext = new FurionGatewayContext(FurionGatewayContext.class);
    }

    private void init() {
        filterManager.init();
    }

    public void start() {

    }


    @Override
    public void addFilter(FurionFilter filter) {
        registry.registerFilter(filter);
    }
}
