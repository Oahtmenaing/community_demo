package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.Annotation.LoginRequired;
import com.my_web.community.community_demo.entity.Event;
import com.my_web.community.community_demo.entity.Page;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.event.EventProducer;
import com.my_web.community.community_demo.service.FollowService;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.Hostholder;
import com.my_web.community.community_demo.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class FollowController implements Community_Constant{
    @Autowired
    private FollowService followService;

    @Autowired
    private User_service userService;

    @Autowired
    Hostholder hostholder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String follow(int entityType, int entityId) {
        User user = hostholder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        // 事件
        Event event = new Event()
                .setUserId(user.getId())
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String unfollow(int entityType, int entityId) {
        User user = hostholder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectById_service(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows_num((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map: userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.selectById_service(userId);
        if(user==null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows_num((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for (Map<String, Object> map: userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if (hostholder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostholder.getUser().getId(), Community_Constant.ENTITY_TYPE_USER, userId);
    }
}
