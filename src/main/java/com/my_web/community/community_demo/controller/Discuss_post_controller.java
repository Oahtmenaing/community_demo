package com.my_web.community.community_demo.controller;


import com.my_web.community.community_demo.entity.Comment;
import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.entity.Page;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.Comment_service;
import com.my_web.community.community_demo.service.Discuss_post_service;
import com.my_web.community.community_demo.service.LikeService;
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

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class Discuss_post_controller implements Community_Constant {

    @Autowired
    private Discuss_post_service discuss_post_service;

    @Autowired
    private Hostholder hostholder;

    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String insert_discuss_post_controller(String title, String content) {
        User user = hostholder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "请登录");
        }

        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setUserId(user.getId());
        post.setContent(content);
        post.setCreateTime(new Date());
        discuss_post_service.insertDiscussPost_service(post);

        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @Autowired
    User_service user_service;

    @Autowired
    Comment_service comment_service;

    @RequestMapping(path="/detail/{postId}", method = RequestMethod.GET)
    public String getPostDetail(@PathVariable("postId") int postId, Model model, Page page){
        DiscussPost post = discuss_post_service.selectById_service(postId);
        if (post == null){
            model.addAttribute("error", "帖子不存在");
        }
        User user = user_service.selectById_service(post.getUserId());
        model.addAttribute("post", post);
        model.addAttribute("user", user);

        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);
        int likeStatus = likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);

        page.setLimit(5);
        page.setRows_num(post.getCommentCount());
        page.setPath("/discuss/detail/" + postId);

        List<Comment> commentList = comment_service.selectByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        List<Map<String, Object>> commentViewList = new ArrayList<>();
        if (commentList != null){
            for (Comment comment : commentList) {
                Map<String, Object> commentView = new HashMap<>();
                commentView.put("comment", comment);
                commentView.put("user", user_service.selectById_service(comment.getUserId()));
                commentView.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));
                commentView.put("likeStatus", likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId()));

                List<Comment> replyList = comment_service.selectByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyViewList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply: replyList) {
                        Map<String, Object> replyView = new HashMap<>();
                        replyView.put("reply", reply);
                        replyView.put("user", user_service.selectById_service(reply.getUserId()));
                        replyView.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                        replyView.put("likeStatus", likeService.findEntityLikeStatus(hostholder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId()));
                        User target = reply.getTargetId() == 0 ? null: user_service.selectById_service(reply.getTargetId());
                        replyView.put("target", target);

                        replyViewList.add(replyView);
                    }
                }
                commentView.put("replys", replyViewList);

                commentView.put("replyCount", comment_service.countByEntity(ENTITY_TYPE_COMMENT, comment.getId()));
                commentViewList.add(commentView);
            }

        }

        model.addAttribute("comments", commentViewList);
        return "site/discuss-detail";
    }
}
