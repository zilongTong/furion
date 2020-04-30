package propertiesTest;

import org.furion.core.context.FurionGatewayContext;

public class Test {
    public static void main(String[] args) {
        //手动指定 配置文件路径
        System.setProperty("config-path", "/Users/wplin/dev/codes/ideaWorkSpace/furion/furion-sample/test/propertiesTest/testProperty.properties");
        FurionGatewayContext context = new FurionGatewayContext();
        MyProperties myProperties = new MyProperties();

        System.out.println(myProperties);
    }
}
