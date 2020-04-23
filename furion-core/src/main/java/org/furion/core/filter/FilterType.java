package org.furion.core.filter;

/**
 * 过滤器类型
 */
public enum FilterType {
    /**
     * 前置过滤器，在请求转发之前调用
     */
    PRE,

    /**
     * 后置过滤器，在转发后、返回客户端之前 调用
     */
    POST;
}
