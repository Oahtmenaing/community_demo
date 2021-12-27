package com.my_web.community.community_demo.DAO;

import com.my_web.community.community_demo.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Message_Mapper {

    // 查询用户私信列表，返回最新私信
    List<Message> selectConversation(int userId, int offset, int limit);

    // 查询当前用户会话数量
    int selectConversationCount(int userId);

    // 查询某个会话包含的所有私信
    List<Message> selectLetterByConversation(String conversationId, int offset,int limit);

    // 某个会话内的私信数量
    int selectLetterCountByConversation(String conversationId);

    // 查询未读私信数量
    int selectUnreadLetterCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    Message selectLatestNotice(int userId, String topic);

    int selectNoticeCount(int userId, String topic);

    int selectNoticeUnread(int userId, String topic);

    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
