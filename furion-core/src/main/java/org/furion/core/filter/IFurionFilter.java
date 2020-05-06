package org.furion.core.filter;

import org.furion.core.exception.FurionException;

public interface IFurionFilter {

    boolean shouldFilter();

    Object run() throws FurionException;
}
