package org.furion.admin.endpoint;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class FurionFilter implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        System.out.println("preHandler");
        httpServletRequest.getHeader("requestId");
        httpServletResponse.setHeader("requestId", httpServletRequest.getHeader("requestId"));
//        System.out.println(((HandlerMethod) handler).getBean().getClass().getName());
//        System.out.println(((HandlerMethod) handler).getMethod().getName());
        httpServletRequest.setAttribute("start", new Date().getTime());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandler");
        System.out.println("request header-----" + httpServletRequest.getHeader("requestId"));

        httpServletResponse.setHeader("requestId", httpServletRequest.getHeader("requestId"));

        System.out.println("response header-----" + httpServletResponse.getHeader("requestId"));
        Long start = (Long) httpServletRequest.getAttribute("start");
        System.out.println("time interceptor 耗时：" + (new Date().getTime() - start));
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        System.out.println("afterCompletion");
        httpServletResponse.setHeader("requestId", httpServletRequest.getHeader("requestId"));
        System.out.println("response header-----" + httpServletResponse.getHeader("requestId"));
        Long start = (Long) httpServletRequest.getAttribute("start");
        System.out.println("time interceptor 耗时：" + (new Date().getTime() - start));
        System.out.println("ex is:" + e);
    }


}
