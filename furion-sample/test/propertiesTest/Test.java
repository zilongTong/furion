package propertiesTest;

import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.FurionProperties;
import org.furion.core.context.properties.PropertiesManager;
import org.furion.core.enumeration.PropertiesSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        //手动指定 配置文件路径
        System.setProperty("config-path", "/Users/wplin/dev/codes/ideaWorkSpace/furion/furion-sample/test/propertiesTest/testProperty.properties");
        FurionGatewayContext context = new FurionGatewayContext();
        MyProperties<String> myProperties = new MyProperties<>();
        System.out.println("初始值key1 = " + myProperties.getKey1());
        PropertiesManager propertiesManager = PropertiesManager.getInstance();

        Properties properties = new Properties();
        properties.put("key1", "刷新后的值v2");
        propertiesManager.refresh(PropertiesSource.LOCAL, properties);
        System.out.println("动态刷新后 key1 = " + myProperties.getKey1());

    }


}
