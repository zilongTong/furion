import org.furion.core.exception.FurionException;
import org.furion.core.filter.FurionFilter;

public class TestDynamicsFilter extends FurionFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public Object run() throws FurionException {
        System.out.println("****** JUST FOR TEST ******");
        return super.run();
    }
}
