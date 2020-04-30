package propertiesTest;

import org.furion.core.context.FurionGatewayContext;

public class Test {

    public static void main(String[] args) {
        FurionGatewayContext context = new FurionGatewayContext();
        context.refresh();
        MyProperties myProperties = new MyProperties();

    }
}
