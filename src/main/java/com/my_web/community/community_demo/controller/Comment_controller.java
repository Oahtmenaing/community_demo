package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.entity.Comment;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Comment_service;
import com.my_web.community.community_demo.util.Hostholder;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class Comment_controller {

    @Autowired
    Hostholder hostholder;

    @Autowired
    Comment_service comment_service;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        User user = hostholder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment_service.addComment(comment);
        return "redirect:/discuss/detail/" + postId;
    }
}
