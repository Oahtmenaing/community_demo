package com.my_web.community.community_demo.controller.interceptor;

import com.my_web.community.community_demo.Annotation.LoginRequired;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    Hostholder hostholder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if(loginRequired != null && hostholder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
