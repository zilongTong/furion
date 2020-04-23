package org.furion.core.filter.load;

import org.furion.core.bean.ClassResourceContainer;
import org.furion.core.context.FurionGatewayContext;

import java.util.List;

/**
 * 网络流filter class加载。admin控制台推送 class文件流
 */
public class NetStreamFilterLoader extends BaseFilterLoader {


    /**
     * 从context中获取 admin地址，建立连接，获取class 文件推送 流
     *
     * @param context
     * @return
     */
    @Override
    List<ClassResourceContainer> getResource(FurionGatewayContext context) {
        return null;
    }
}
