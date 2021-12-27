package com.my_web.community.community_demo.service;

import com.my_web.community.community_demo.DAO.Login_ticket_Mapper;
import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.entity.Activation_result;
import com.my_web.community.community_demo.entity.Login_ticket;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.MailClient;
import com.my_web.community.community_demo.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class User_service implements Activation_result {
    @Autowired
    User_Mapper user_mapper;

    public User selectById_service(int id) {
        //return user_mapper.selectById(id);

        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;
    }

    @Autowired
    CommunityUtil communityUtil;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String context_path;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//   Login_ticket_Mapper login_ticket_mapper;

    public Map<String, Object> register_helper(User user) {
        Map<String, Object>register_map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("输入不能为空");
        }

        if (StringUtils.isBlank(user.getUsername())){
            register_map.put("username_message", "用户名不得为空");
            return register_map;
        }

        if (StringUtils.isBlank(user.getPassword())){
            register_map.put("password_message", "密码不得为空");
            return register_map;
        }

        if (StringUtils.isBlank(user.getEmail())){
            register_map.put("email_message", "邮箱不得为空");
            return register_map;
        }

        //用户名验证
        User user_checker = user_mapper.selectByName(user.getUsername());
        if (user_checker != null){
            register_map.put("username_message", "用户名已被占用");
            return register_map;
        }

        //验证邮箱
        user_checker = user_mapper.selectByEmail(user.getEmail());
        if (user_checker != null){
            register_map.put("email_message", "邮箱已被占用");
            return register_map;
        }

        //注册用户
        user.setSalt(communityUtil.createRandomId().substring(0, 5));
        user.setPassword(communityUtil.MD5_Transer(user.getPassword() + user.getSalt()));
        user.setStatus(0);
        user.setType(0);
        user.setActivation_code(communityUtil.createRandomId());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        user_mapper.insertUser(user);

        //发送邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + context_path + "/activation/" + user.getId() + "/" + user.getActivation_code();
        context.setVariable("url", url);
        System.out.println(url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), content, "Activation Mail");

        return register_map;
    }

    public int Activation(int userid, String code) {
        User user = user_mapper.selectById(userid);
        if (user.getStatus() == 1){
            return Activation_repeat;
        }else if (user.getActivation_code().equals(code)){
            user_mapper.updateStatus(userid, 1);
            cleanCache(userid);
            return Activation_success;
        } else{
            return Activation_failed;
        }
    }

    public Map<String, Object> login(String username, String password, int expiredSecond) {
        Map<String, Object> map = new HashMap<>();
        //空值验证
        if (StringUtils.isBlank(username)){
            map.put("name_message", "用户名不得为空");
            return map;
        }

        if (StringUtils.isBlank(password)){
            map.put("password_message", "密码不得为空");
            return map;
        }
        //用户名验证
        User user = user_mapper.selectByName(username);
        if (user == null){
            map.put("name_message", "用户名不存在");
            return map;
        }

        if (user.getStatus() == 0){
            map.put("name_message", "该用户未激活");
            return map;
        }

        //密码验证
        if (!communityUtil.MD5_Transer(password + user.getSalt()).equals(user.getPassword())){
            map.put("password_message", "密码错误");
            return map;
        }

        //登陆凭证
        Login_ticket login_ticket = new Login_ticket();
        login_ticket.setUserId(user.getId());
        login_ticket.setStatus(0);
        login_ticket.setTicket(communityUtil.createRandomId());
        login_ticket.setExpired(new Date(System.currentTimeMillis() + expiredSecond * 1000));
        map.put("ticket", login_ticket.getTicket());
//        login_ticket_mapper.insert_login_ticket(login_ticket);
        String redisKey = RedisKeyUtil.getTicketKey(login_ticket.getTicket());
        redisTemplate.opsForValue().set(redisKey, login_ticket);

        return map;
    }

    public void log_out(String ticket) {
        //login_ticket_mapper.update_status(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        Login_ticket loginTicket = (Login_ticket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    public Login_ticket get_ticket_service(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (Login_ticket) redisTemplate.opsForValue().get(redisKey);
    }

    public void updateHeader_image(int user_id, String header_url){
        user_mapper.updateHeaderUrl(user_id, header_url);
        cleanCache(user_id);
    }

    public void updatePassword(int user_id, String password) {
        user_mapper.updatePassword(user_id, password);
        cleanCache(user_id);
    }

    public User selectByName(String name) { return user_mapper.selectByName(name); }

    // Redis缓存User信息部分
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }

    private User initCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user = user_mapper.selectById(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    private void cleanCache(int userId) {
        redisTemplate.delete(RedisKeyUtil.getUserKey(userId));
    }
}
