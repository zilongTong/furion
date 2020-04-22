package org.furion.core.filter;


import org.furion.core.context.FurionFilterResult;
import org.furion.core.exception.FurionException;

public abstract class FurionFilter implements IFurionFilter, Comparable<FurionFilter> {


    public FurionFilter() {
    }

    public abstract FilterType filterType();

    public abstract int filterOrder();

    public boolean isStaticFilter() {
        return true;
    }

    public boolean isFilterDisabled() {
        return false;
    }


    public FurionFilterResult runFilter() {
        FurionFilterResult zr = new FurionFilterResult();
        if (!this.isFilterDisabled()) {
            if (this.shouldFilter()) {
//                Tracer t = TracerFactory.instance().startMicroTracer("ZUUL::" + this.getClass().getSimpleName());

                try {
                    Object res = this.run();
                    zr = new FurionFilterResult(res, FurionException.Status.SUCCESS);
                } catch (Throwable var7) {
//                    t.setName("ZUUL::" + this.getClass().getSimpleName() + " failed");
                    zr = new FurionFilterResult(FurionException.Status.FAILED);
                    zr.setException(var7);
                } finally {
//                    t.stopAndLog();
                }
            } else {
                zr = new FurionFilterResult(FurionException.Status.SKIPPED);
            }
        }

        return zr;
    }

    public int compareTo(FurionFilter filter) {
        return Integer.compare(this.filterOrder(), filter.filterOrder());
    }


}
