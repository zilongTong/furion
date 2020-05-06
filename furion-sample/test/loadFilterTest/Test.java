package loadFilterTest;

import org.furion.core.context.FurionGatewayContext;

public class Test {
    public static void main(String[] args) {
        System.setProperty("filter.path", "");
        FurionGatewayContext furionGatewaContext = new FurionGatewayContext(Test.class);
        furionGatewaContext.start();
        while (true) {

        }
    }
}
