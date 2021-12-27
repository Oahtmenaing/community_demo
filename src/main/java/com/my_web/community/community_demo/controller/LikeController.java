package com.my_web.community.community_demo.controller;

import com.my_web.community.community_demo.entity.Event;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.event.EventProducer;
import com.my_web.community.community_demo.service.LikeService;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements Community_Constant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostholder.getUser();

        likeService.like(user.getId(), entityType, entityId, entityUserId);

        long likeCount = likeService.findEntityLikeCount(entityType, entityId);

        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostholder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("post", postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
