package com.my_web.community.community_demo.config;

import com.my_web.community.community_demo.controller.interceptor.AlphaInterceptor;
import com.my_web.community.community_demo.controller.interceptor.LoginRequiredInterceptor;
import com.my_web.community.community_demo.controller.interceptor.Login_Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private Login_Interceptor login_interceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(login_interceptor)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.png","/**/*.jpeg", "/login", "/register");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.png","/**/*.jpeg");
    }


}
