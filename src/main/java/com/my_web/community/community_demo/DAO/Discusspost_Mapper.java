package com.my_web.community.community_demo.DAO;

import com.my_web.community.community_demo.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface Discusspost_Mapper {
    List<DiscussPost> select_discuss_post(int userId, int offset, int limit);

    int count_discuss_post(@Param("userId") int userId);

    int insert_discuss_post(DiscussPost discussPost);

    DiscussPost selectById(int id);

    int updateCommentCount (int id, int count);
}
