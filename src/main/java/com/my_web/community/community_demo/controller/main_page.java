package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.entity.Page;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Discuss_post_service;
import com.my_web.community.community_demo.service.LikeService;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Community_Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class main_page implements Community_Constant {
    @Autowired
    private Discuss_post_service discuss_post_service;
    @Autowired
    private User_service user_service;
    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/main", method = RequestMethod.GET)
    public String select_discuss_post_controller(Model model, Page page) {
        page.setRows_num(discuss_post_service.discussPost_count_service(0));
        page.setPath("/main");
        List<Map<String, Object>> post_user_list = new ArrayList<>();
        List<DiscussPost> posts_list =  discuss_post_service.discussPost_select_service(0, page.getOffset(), page.getLimit());
        for(DiscussPost post: posts_list){
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);
            User user = user_service.selectById_service(post.getUserId());
            map.put("user", user);

            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);

            post_user_list.add(map);
        }
        model.addAttribute("post_user_list", post_user_list);
        for(Object post: post_user_list){
            System.out.println(post);
        }
        System.out.println(page.getFrom());
        System.out.println(page.getTo());
        System.out.println(page.getTotal_page());
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
