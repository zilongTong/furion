package org.furion.core.filter.load;

import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;

/**
 * 加载Filter，并实例化，存入Context 中
 * 都为单例
 */
public interface IFilterLoader {

    /**
     * 自己完成class 资源寻找、加载、注册一条龙
     * @param context
     */
    void loadFilter(FurionGatewayContext context);

    /**
     * 外部提供 ClassResourceContainer，完成加载类、注册
     */
    default void loadFilter(FurionGatewayContext context, ClassResourceContainer classResourceContainer) {
    }


}
