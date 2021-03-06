package com.my_web.community.community_demo.controller;

import com.google.code.kaptcha.Producer;
import com.my_web.community.community_demo.entity.Activation_result;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.CookieUtil;
import com.my_web.community.community_demo.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Controller
public class Register_controller implements Activation_result {

    private static final Logger logger = LoggerFactory.getLogger(Register_controller.class);

    @Value("server.servlet.context-path")
    private String context_path;

    @Autowired
    User_service user_service;

    @Autowired
    CommunityUtil communityUtil;

    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String register_page() {
        return "/site/register";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String login_page() {
        return "/site/login";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> register_map = user_service.register_helper(user);
        if (register_map == null || register_map.isEmpty()){
            model.addAttribute("msg", "????????????");
            model.addAttribute("target", "/main");
            return "/site/operate-result";
        }else{
            model.addAttribute("username_message", register_map.get("username_message"));
            model.addAttribute("password_message", register_map.get("password_message"));
            model.addAttribute("email_message", register_map.get("email_message"));
            return "/site/register";
        }
    }

    @RequestMapping(path = "/activation/{userid}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userid") int userid, @PathVariable("code") String code) {
        int activation_result = user_service.Activation(userid, code);
        if (activation_result == Activation_success){
            model.addAttribute("msg", "????????????");
            model.addAttribute("target", "/login");
        }else if (activation_result == Activation_failed){
            model.addAttribute("msg", "????????????");
            model.addAttribute("target", "/index");
        }else{
            model.addAttribute("msg", "????????????");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @Autowired
    Producer kaptcha_producer;

    @Autowired
    RedisTemplate redisTemplate;

    //???????????????
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    public void kaptcha_image(HttpServletResponse response/*, HttpSession session*/) {
        String text = kaptcha_producer.createText();
        BufferedImage image = kaptcha_producer.createImage(text);

        //session.setAttribute("kaptcha", text);

        String kaptchaOwner = communityUtil.createRandomId();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setPath(context_path);
        cookie.setMaxAge(60);
        response.addCookie(cookie);

        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        response.setContentType("image/png");
        try {
            OutputStream outstream = response.getOutputStream();
            ImageIO.write(image, "png", outstream);
        } catch (IOException e) {
            logger.error("????????????????????? " + e.getMessage());
        }
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code, boolean remember_me/*, HttpSession session*/,
                        HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner) {

        //???????????????
        //String kaptch = (String)session.getAttribute("kaptcha");

        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String)redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(code) || StringUtils.isBlank(kaptcha) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("code_message", "???????????????");
            return "/site/login";
        }

        //????????????
        int expiredSecond = remember_me?Remembered_expired_time: Default_expired_time;
        Map<String, Object> map = user_service.login(username, password, expiredSecond);
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(context_path);
            cookie.setMaxAge(expiredSecond);
            response.addCookie(cookie);
            return "redirect:/main";
        }else{
            model.addAttribute("username_message", map.get("name_message"));
            model.addAttribute("password_message", map.get("password_message"));
            return "/site/login";
        }

    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        user_service.log_out(ticket);
        return "redirect:/login";
    }
}
