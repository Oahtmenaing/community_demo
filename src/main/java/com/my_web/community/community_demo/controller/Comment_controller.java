package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.entity.Comment;
import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.entity.Event;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.event.EventProducer;
import com.my_web.community.community_demo.service.Comment_service;
import com.my_web.community.community_demo.service.Discuss_post_service;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class Comment_controller implements Community_Constant {

    @Autowired
    Hostholder hostholder;

    @Autowired
    Comment_service comment_service;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private Discuss_post_service discussPostService;

    @RequestMapping(path = "/add/{postId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        User user = hostholder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment_service.addComment(comment);

        // 触发事件
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostholder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", postId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.selectById_service(postId);
            event.setEntityUserId(target.getUserId());
        } else {
            Comment target = comment_service.selectById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        return "redirect:/discuss/detail/" + postId;
    }
}
