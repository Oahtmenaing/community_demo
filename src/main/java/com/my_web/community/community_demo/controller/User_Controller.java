package com.my_web.community.community_demo.controller;


import com.my_web.community.community_demo.Annotation.LoginRequired;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.FollowService;
import com.my_web.community.community_demo.service.LikeService;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Community_Constant;
import com.my_web.community.community_demo.util.Hostholder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class User_Controller implements Community_Constant {
    private Logger logger = LoggerFactory.getLogger(User_Controller.class);

    @Autowired
    LikeService likeService;

    @Autowired
    Hostholder hostholder;

    @Autowired
    CommunityUtil communityUtil;

    @Autowired
    User_service user_service;

    @Value("${community.path.upload}")
    String upload_path;

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String context_path;

    @LoginRequired
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String setting_page() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(value = "/setting/upload", method = RequestMethod.POST)
    public String header_upload(MultipartFile new_header_file, Model model) {
        if(new_header_file == null) {
            model.addAttribute("error", "?????????????????????");
            return "/site/setting";
        }

        User user = hostholder.getUser();
        String new_header_name = new_header_file.getOriginalFilename();
        String suffix = new_header_name.substring(new_header_name.lastIndexOf("."));
        if (suffix == null){
            model.addAttribute("error", "??????????????????");
            return "/site/setting";
        }
        new_header_name = communityUtil.createRandomId() + suffix;

        //??????????????????
        File dest_path = new File(upload_path + "/" + new_header_name);
        try {
            new_header_file.transferTo(dest_path);

        } catch (IOException e) {
            logger.error("??????????????????:" + e.getMessage());
            throw new RuntimeException("??????????????????", e);
        }

        //????????????????????????
        //http://localhost8080/community_demo/user/header/xxx(.png,.jpg)
        String headerUrl = domain + context_path + "/user/header/" + new_header_name;
        user_service.updateHeader_image(user.getId(), headerUrl);

        return "redirect:/main";
    }


    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void get_header_image(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = upload_path + '/' + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        // ????????????
        response.setContentType("image/" + suffix);
        try (FileInputStream fis = new FileInputStream(filename);)
        {
            OutputStream os = response.getOutputStream();

            byte[] buffer = new byte[1024];
            int b;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer, 0 ,b);
            }
        } catch (Exception e) {
            logger.error("?????????????????????" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/setting/updatepassword", method = RequestMethod.POST)
    public String update_password(Model model, String password_ori, String password_new, String password_rep) {
        User user = hostholder.getUser();
        if (!communityUtil.MD5_Transer(password_ori + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("password_ori_error", "????????????");
            return "/site/setting";
        }
        if (communityUtil.MD5_Transer(password_new + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("password_new_error", "?????????????????????");
            return "/site/setting";
        }
        if (!password_rep.equals(password_new)) {
            model.addAttribute("password_rep_error", "?????????????????????????????????");
            return "/site/setting";
        }

        user_service.updatePassword(user.getId(), communityUtil.MD5_Transer(password_new + user.getSalt()));
        return "redirect:/main";
    }

    @Autowired
    FollowService followService;

    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = user_service.selectById_service(userId);
        if (user == null) {
            throw new RuntimeException("???????????????");
        }
        model.addAttribute("user", user);
        // ??????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // ??????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // ??????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // ????????????
        boolean hasFollowed = false;
        if (hostholder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostholder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
