package com.my_web.community.community_demo.controller;


import com.alibaba.fastjson.JSONObject;
import com.my_web.community.community_demo.Annotation.LoginRequired;
import com.my_web.community.community_demo.entity.Message;
import com.my_web.community.community_demo.entity.Page;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Message_service;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.Hostholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class Message_controller implements Community_Constant {
    @Autowired
    Message_service message_service;

    @Autowired
    Hostholder hostholder;

    @Autowired
    User_service user_service;

    @LoginRequired
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
        int noticeUnread = message_service.countNoticeUnread(user.getId(), null);
        model.addAttribute("noticeUnread", noticeUnread);

        return "/site/letter";
    }

    @LoginRequired
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

        //将消息设为已读
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

    @LoginRequired
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

    @LoginRequired
    @RequestMapping(path = "/letter/delete", method = RequestMethod.POST)
    @ResponseBody
    public String deleteMessage(String id) {
        if (id != null) {
            List<Integer> ids = new ArrayList<>();
            ids.add(Integer.parseInt(id));
            message_service.deleteMessage(ids);
        }
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String NoticeList(Model model) {
        User user = hostholder.getUser();

        // 评论类通知
        Message message = message_service.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if(message != null) {
            Map<String, Object> commentView = new HashMap<>();
            commentView.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            commentView.put("user", user_service.selectById_service((Integer) data.get("userId")));
            commentView.put("entityType", data.get("entityType"));
            commentView.put("entityId", data.get("entityId"));
            commentView.put("postId", data.get("postId"));

            int count = message_service.countNotice(user.getId(), TOPIC_COMMENT);
            commentView.put("count", count);

            int unread = message_service.countNoticeUnread(user.getId(), TOPIC_COMMENT);
            commentView.put("unread", unread);
            model.addAttribute("commentNotice", commentView);
        }

        // 点赞
        message = message_service.findLatestNotice(user.getId(), TOPIC_LIKE);
        if(message != null) {
            Map<String, Object>likeView = new HashMap<>();
            likeView.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            likeView.put("user", user_service.selectById_service((Integer) data.get("userId")));
            likeView.put("entityType", data.get("entityType"));
            likeView.put("entityId", data.get("entityId"));
            likeView.put("postId", data.get("postId"));

            int count = message_service.countNotice(user.getId(), TOPIC_LIKE);
            likeView.put("count", count);

            int unread = message_service.countNoticeUnread(user.getId(), TOPIC_LIKE);
            likeView.put("unread", unread);
            model.addAttribute("likeNotice", likeView);
        }

        message = message_service.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if(message != null) {
            Map<String, Object>followView = new HashMap<>();
            followView.put("message", message);

            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            followView.put("user", user_service.selectById_service((Integer) data.get("userId")));
            followView.put("entityType", data.get("entityType"));
            followView.put("entityId", data.get("entityId"));

            int count = message_service.countNotice(user.getId(), TOPIC_FOLLOW);
            followView.put("count", count);

            int unread = message_service.countNoticeUnread(user.getId(), TOPIC_FOLLOW);
            followView.put("unread", unread);
            model.addAttribute("followNotice", followView);
        }

        // 查询未读消息
        int letterUnread = message_service.selectUnreadLetterCount(user.getId(), null);
        model.addAttribute("letterUnread", letterUnread);
        int noticeUnread = message_service.countNoticeUnread(user.getId(), null);
        model.addAttribute("noticeUnread", noticeUnread);

        return "/site/notice";
    }


    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeDetail (@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostholder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail" + topic);
        page.setRows_num(message_service.countNotice(user.getId(), topic));

        List<Message> noticeList = message_service.selectNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice: noticeList) {
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", user_service.selectById_service((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                map.put("fromUser", user_service.selectById_service(notice.getFromId()));

                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        List<Integer> ids = GetUnreadLetters(noticeList);
        if (!ids.isEmpty()) {
            message_service.readMessage(ids);
        }
        return "site/notice-detail";
    }
}
