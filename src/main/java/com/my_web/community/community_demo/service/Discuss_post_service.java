package com.my_web.community.community_demo.service;

import com.my_web.community.community_demo.DAO.Discusspost_Mapper;
import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class Discuss_post_service {

    @Autowired
    private Discusspost_Mapper discusspost_mapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;


    public List<DiscussPost> discussPost_select_service(int userId, int offset, int limit) {
        return discusspost_mapper.select_discuss_post(userId, offset, limit);
    }

    public int discussPost_count_service(int userId) {
        return discusspost_mapper.count_discuss_post(userId);
    }


    public int insertDiscussPost_service(DiscussPost post) {
        if (post == null){
            throw new IllegalArgumentException("参数不得为空");
        }

        // 转义HTML标记（使得特定特殊字符能够忽视HTML规则从而能以原输入字符展示）
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discusspost_mapper.insert_discuss_post(post);
    }

    public DiscussPost selectById_service(int id) {
        return discusspost_mapper.selectById(id);

    }
}
