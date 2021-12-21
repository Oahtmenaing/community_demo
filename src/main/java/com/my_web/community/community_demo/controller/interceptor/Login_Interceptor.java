package com.my_web.community.community_demo.controller.interceptor;

import com.my_web.community.community_demo.entity.Login_ticket;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CookieUtil;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class Login_Interceptor implements HandlerInterceptor {

    @Autowired
    User_service user_service;

    @Autowired
    Hostholder hostholder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.get_value(request, "ticket");
        if (ticket != null){
            Login_ticket login_ticket = user_service.get_ticket_service(ticket);
            if (login_ticket.getStatus() == 0 && login_ticket.getExpired().after(new Date()) && login_ticket != null){
                User user = user_service.selectById_service(login_ticket.getUserId());
                hostholder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostholder.getUser();
        if (user != null && modelAndView !=null){
            modelAndView.addObject("login_user", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostholder.cleanUser();
    }
}
