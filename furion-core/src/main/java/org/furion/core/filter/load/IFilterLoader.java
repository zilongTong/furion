package org.furion.core.filter.load;

import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;
import org.furion.core.filter.FurionFilter;
import sun.misc.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 加载Filter，并实例化，存入Context 中
 * 都为单例
 */
public interface IFilterLoader {

    void loadFilter(FurionGatewayContext context);

    /**
     * @param context
     * @param classResourceContainer
     */
    default void loadFilter(FurionGatewayContext context, ClassResourceContainer classResourceContainer) {
    }


}
