package loadFilterTest;

import org.furion.core.exception.FurionException;
import org.furion.core.filter.FilterType;
import org.furion.core.filter.FurionFilter;

public class ProjectPackageFilter extends FurionFilter {
    @Override
    public String filterType() {
        return FilterType.POST.toString();
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public Object run() throws FurionException {
        System.out.println("run run run");
        return null;
    }
}
