package com.my_web.community.community_demo.service;

import com.my_web.community.community_demo.DAO.Message_Mapper;
import com.my_web.community.community_demo.entity.Message;
import com.my_web.community.community_demo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class Message_service {
    @Autowired
    Message_Mapper message_mapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Message> selectConversation(int userId, int offset, int limit){
        return message_mapper.selectConversation(userId, offset, limit);
    }

    public int selectConversationCount(int userId){
        return message_mapper.selectConversationCount(userId);
    }

    public List<Message> selectLetterByConversation(String conversationId, int offset,int limit){
        return message_mapper.selectLetterByConversation(conversationId, offset, limit);
    }

    public int selectLetterCountByConversation(String conversationId) {
        return message_mapper.selectLetterCountByConversation(conversationId);
    }

    public int selectUnreadLetterCount(int userId, String conversationId) {
        return message_mapper.selectUnreadLetterCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return message_mapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        return message_mapper.updateStatus(ids, 1);
    }

    public int deleteMessage(List<Integer> ids) {
        return message_mapper.updateStatus(ids, 2);
    }
}
