package com.my_web.community.community_demo.DAO;

import com.my_web.community.community_demo.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface Comment_Mapper {

    List<Comment> selectByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    Comment selectById(int id);
}
