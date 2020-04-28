package org.furion.core.filter;


import io.netty.channel.Channel;
import org.furion.core.exception.FurionException;


public abstract class FurionFilter implements IFurionFilter, Comparable<FurionFilter> {


    public FurionFilter() {
    }

    private transient boolean whetherWriteAndFlush;

    private transient Long exclusiveOwnerRequest;

    private transient Channel exclusiveOwnerChannel;


    protected void writeAndFlush(Object o) {
        exclusiveOwnerChannel.writeAndFlush(o);
        whetherWriteAndFlush = true;
        //ReferenceCountUtil.release(o);
    }

    public abstract String filterType(
    );

    public abstract int filterOrder();

    public boolean isStaticFilter() {
        return true;
    }

    public boolean isFilterDisabled() {
        return false;
    }

    public Channel getExclusiveOwnerChannel() {
        return exclusiveOwnerChannel;
    }

    public void setExclusiveOwnerChannel(Channel exclusiveOwnerChannel) {
        this.exclusiveOwnerChannel = exclusiveOwnerChannel;
    }

    protected FurionFilter init(Long id, Channel channel) {
        this.exclusiveOwnerRequest = id;
        this.exclusiveOwnerChannel = channel;
        return this;
    }

    @Override
    public boolean shouldFilter() {
        return false;
    }

    protected boolean shouldFilter0() {
        if (filterType().equalsIgnoreCase(FilterType.PRE.name())) {
            return shouldFilter() && whetherWriteAndFlush;
        }
        if (filterType().equalsIgnoreCase(FilterType.POST.name())) {
            return shouldFilter();
        }
        return false;
    }

    @Override
    public Object run() throws FurionException {
        return null;
    }

    public Long getExclusiveOwnerRequest() {
        return exclusiveOwnerRequest;
    }

    public void setExclusiveOwnerRequest(Long exclusiveOwnerRequest) {
        this.exclusiveOwnerRequest = exclusiveOwnerRequest;
    }

//    public FurionFilterResult runFilter() {
//        FurionFilterResult zr = new FurionFilterResult();
//        if (!this.isFilterDisabled()) {
//            if (this.shouldFilter()) {
////                Tracer t = TracerFactory.instance().startMicroTracer("ZUUL::" + this.getClass().getSimpleName());
//
//                try {
//                    Object res = this.run();
//                    zr = new FurionFilterResult(res, FurionException.Status.SUCCESS);
//                } catch (Throwable var7) {
////                    t.setName("ZUUL::" + this.getClass().getSimpleName() + " failed");
//                    zr = new FurionFilterResult(FurionException.Status.FAILED);
//                    zr.setException(var7);
//                } finally {
////                    t.stopAndLog();
//                }
//            } else {
//                zr = new FurionFilterResult(FurionException.Status.SKIPPED);
//            }
//        }
//
//        return zr;
//    }

    public int compareTo(FurionFilter filter) {
        return Integer.compare(this.filterOrder(), filter.filterOrder());
    }


}
