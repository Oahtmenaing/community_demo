package com.my_web.community.community_demo.controller;


import com.my_web.community.community_demo.Annotation.LoginRequired;
import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.service.User_service;
import com.my_web.community.community_demo.util.CommunityUtil;
import com.my_web.community.community_demo.util.Hostholder;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
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
import java.net.http.HttpResponse;

@Controller
@RequestMapping("/user")
public class User_Controller {
    private Logger logger = LoggerFactory.getLogger(User_Controller.class);

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
            model.addAttribute("error", "请输入图片路径");
            return "/site/setting";
        }

        User user = hostholder.getUser();
        String new_header_name = new_header_file.getOriginalFilename();
        String suffix = new_header_name.substring(new_header_name.lastIndexOf("."));
        if (suffix == null){
            model.addAttribute("error", "图片格式错误");
            return "/site/setting";
        }
        new_header_name = communityUtil.createRandomId() + suffix;

        //文件存放路径
        File dest_path = new File(upload_path + "/" + new_header_name);
        try {
            new_header_file.transferTo(dest_path);

        } catch (IOException e) {
            logger.error("文件保存失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败", e);
        }

        //修改文件外部路径
        //http://localhost8080/community_demo/user/header/xxx(.png,.jpg)
        String headerUrl = domain + context_path + "/user/header/" + new_header_name;
        user_service.updateHeader_image(user.getId(), headerUrl);

        return "redirect:/main";
    }


    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void get_header_image(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = upload_path + '/' + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 相应图片
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
            logger.error("获取图片失败：" + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path = "/setting/updatepassword", method = RequestMethod.POST)
    public String update_password(Model model, String password_ori, String password_new, String password_rep) {
        User user = hostholder.getUser();
        if (!communityUtil.MD5_Transer(password_ori + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("password_ori_error", "密码错误");
            return "/site/setting";
        }
        if (communityUtil.MD5_Transer(password_new + user.getSalt()).equals(user.getPassword())) {
            model.addAttribute("password_new_error", "不可使用原密码");
            return "/site/setting";
        }
        if (!password_rep.equals(password_new)) {
            model.addAttribute("password_rep_error", "两次密码不相同，请确认");
            return "/site/setting";
        }

        user_service.updatePassword(user.getId(), communityUtil.MD5_Transer(password_new + user.getSalt()));
        return "redirect:/main";
    }
}
