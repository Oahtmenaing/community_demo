package com.my_web.community.community_demo.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AlphaInterceptor implements HandlerInterceptor {

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);


    // 在controller执行之前
    // preHandle如果返回False 将会直接对程序进行拦截，阻止Controller进行。
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.error("preHandle:" + handler.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.error("postHandle:" + handler.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.error("postHandle:" + handler.toString());
    }
}
