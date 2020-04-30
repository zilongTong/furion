package propertiesTest;

import org.furion.core.context.FurionGatewayContext;

public class Test {
    public static void main(String[] args) {
        System.setProperty("config-path", "/Users/wplin/dev/codes/ideaWorkSpace/furion/furion-sample/test/propertiesTest/testProperty.properties");
        FurionGatewayContext context = new FurionGatewayContext();
        MyProperties myProperties = new MyProperties();

        System.out.println(myProperties);
    }
}
