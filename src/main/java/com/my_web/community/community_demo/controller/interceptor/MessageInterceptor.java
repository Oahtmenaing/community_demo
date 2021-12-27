package com.my_web.community.community_demo.controller.interceptor;

import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Message_service;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private Message_service messageService;

    @Override
    public void postHandle (HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        User user = hostholder.getUser();
        if (user!= null && modelAndView != null) {
            int letterUnreadCount = messageService.selectUnreadLetterCount(user.getId(), null);
            int noticeUnreadCount = messageService.countNoticeUnread(user.getId(), null);
            modelAndView.addObject("unreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
