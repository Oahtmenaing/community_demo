package com.my_web.community.community_demo.controller;


import com.my_web.community.community_demo.DAO.Message_Mapper;
import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.entity.Message;
import com.my_web.community.community_demo.entity.Page;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Message_service;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class Message_controller {
    @Autowired
    Message_service message_service;

    @Autowired
    Hostholder hostholder;

    @Autowired
    User_service user_service;

    @RequestMapping(path = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostholder.getUser();
        page.setLimit(5);
        page.setPath("letter/list");
        page.setRows_num(message_service.selectConversationCount(user.getId()));

        List<Message> messageList = message_service.selectConversation(user.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> conversations = new ArrayList<>();
        if (messageList != null) {
            for (Message message: messageList) {
                Map<String, Object> conversation = new HashMap<>();
                conversation.put("conversation", message);
                conversation.put("unreadCount", message_service.selectUnreadLetterCount(user.getId(),
                        message.getConversationId()));
                conversation.put("letterCount", message_service.selectLetterCountByConversation(message.getConversationId()));
                int target_id = message.getToId() == user.getId()? message.getFromId():message.getToId();
                conversation.put("targetUser", user_service.selectById_service(target_id));

                conversations.add(conversation);
            }
        }
        model.addAttribute("conversations", conversations);

        // 未读消息总数
        int totalUnreadMessage = message_service.selectUnreadLetterCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", totalUnreadMessage);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows_num(message_service.selectLetterCountByConversation(conversationId));

        List<Message> letterList = message_service.selectLetterByConversation(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null){
            for (Message letter:letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("from_user", user_service.selectById_service(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("targetUser", getTarget(conversationId));

        List<Integer> ids = GetUnreadLetters(letterList);
        if (!ids.isEmpty()) {
            message_service.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private List<Integer> GetUnreadLetters(List<Message> letters) {
        List<Integer> ids = new ArrayList<>();
        if (letters != null) {
            for (Message letter: letters) {
                if (hostholder.getUser().getId() == letter.getToId() && letter.getStatus() == 0) {
                    ids.add(letter.getId());
                }
            }
        }
        return ids;
    }

    User getTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostholder.getUser().getId() == id0) {
            return user_service.selectById_service(id1);
        } else {
            return user_service.selectById_service(id0);
        }
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User toUser = user_service.selectByName(toName);
        if (toUser == null) {
            return CommunityUtil.getJSONString(1,"目标用户不存在");
        }
        User fromUser = hostholder.getUser();
        Message message = new Message();
        message.setFromId(fromUser.getId());
        message.setToId(toUser.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        message_service.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
