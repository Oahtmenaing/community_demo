package com.my_web.community.community_demo.service;

import com.my_web.community.community_demo.DAO.Comment_Mapper;
import com.my_web.community.community_demo.DAO.Discusspost_Mapper;
import com.my_web.community.community_demo.entity.Comment;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class Comment_service implements Community_Constant {

    @Autowired
    Comment_Mapper comment_mapper;

    @Autowired
    Discusspost_Mapper discusspost_mapper;

    @Autowired
    SensitiveFilter sensitiveFilter;

    public List<Comment> selectByEntity(int entityType, int entityId, int offset, int limit) {
        return comment_mapper.selectByEntity(entityType, entityId, offset, limit);
    }

    public int countByEntity(int entityType, int entityId) {
        return comment_mapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("参数不得为空");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = comment_mapper.insertComment(comment);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = comment_mapper.selectCountByEntity(ENTITY_TYPE_POST, comment.getEntityId());
            discusspost_mapper.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    public Comment selectById(int id) {
        return comment_mapper.selectById(id);
    }
}
