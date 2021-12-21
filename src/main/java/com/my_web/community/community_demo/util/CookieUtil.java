package com.my_web.community.community_demo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String get_value(HttpServletRequest request, String name){
        if (request == null|| name == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null){
            throw new IllegalArgumentException("不包含Cookie");
        }else{
            for (Cookie cookie: cookies){
                if (cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
