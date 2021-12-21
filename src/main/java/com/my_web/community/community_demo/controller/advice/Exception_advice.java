package com.my_web.community.community_demo.controller.advice;


import com.my_web.community.community_demo.util.CommunityUtil;
import org.apache.coyote.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.HttpRequest;

@ControllerAdvice(annotations = Controller.class)
public class Exception_advice {

    private static final Logger logger = LoggerFactory.getLogger(Exception_advice.class);

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletResponse response, HttpServletRequest request) throws IOException {
        logger.error("服务器异常:" + e.getMessage());
        for (StackTraceElement element: e.getStackTrace()){
            logger.error(element.toString());
        }

        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(CommunityUtil.getJSONString(1, "服务器异常"));
        } else {
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
