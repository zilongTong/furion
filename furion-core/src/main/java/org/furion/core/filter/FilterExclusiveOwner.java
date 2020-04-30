package org.furion.core.filter;

import io.netty.channel.Channel;

public class FilterExclusiveOwner {

    private transient boolean whetherWriteAndFlush;

    private transient Long exclusiveOwnerRequest;

    private transient Channel exclusiveOwnerChannel;

    public FilterExclusiveOwner(boolean whetherWriteAndFlush, Long exclusiveOwnerRequest, Channel exclusiveOwnerChannel) {
        this.whetherWriteAndFlush = whetherWriteAndFlush;
        this.exclusiveOwnerRequest = exclusiveOwnerRequest;
        this.exclusiveOwnerChannel = exclusiveOwnerChannel;
    }

    public boolean isWhetherWriteAndFlush() {
        return whetherWriteAndFlush;
    }

    public void setWhetherWriteAndFlush(boolean whetherWriteAndFlush) {
        this.whetherWriteAndFlush = whetherWriteAndFlush;
    }

    public Long getExclusiveOwnerRequest() {
        return exclusiveOwnerRequest;
    }

    public void setExclusiveOwnerRequest(Long exclusiveOwnerRequest) {
        this.exclusiveOwnerRequest = exclusiveOwnerRequest;
    }

    public Channel getExclusiveOwnerChannel() {
        return exclusiveOwnerChannel;
    }

    public void setExclusiveOwnerChannel(Channel exclusiveOwnerChannel) {
        this.exclusiveOwnerChannel = exclusiveOwnerChannel;
    }
}
