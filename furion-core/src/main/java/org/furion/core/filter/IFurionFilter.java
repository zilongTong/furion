package org.furion.core.filter;

import io.netty.channel.Channel;
import org.furion.core.exception.FurionException;

public interface IFurionFilter {

    boolean shouldFilter(Long id);

    Object run(Long id, Channel channel) throws FurionException;
}
