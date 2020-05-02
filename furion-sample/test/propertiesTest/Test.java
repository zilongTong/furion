package propertiesTest;

import org.furion.core.context.FurionGatewayContext;
import org.furion.core.context.FurionProperties;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class Test {
    public static void main(String[] args) {
        //手动指定 配置文件路径
        System.setProperty("config-path", "/Users/wplin/dev/codes/ideaWorkSpace/furion/furion-sample/test/propertiesTest/testProperty.properties");
        FurionGatewayContext context = new FurionGatewayContext();
        MyProperties myProperties = new MyProperties();

        System.out.println(myProperties);

    }


}
