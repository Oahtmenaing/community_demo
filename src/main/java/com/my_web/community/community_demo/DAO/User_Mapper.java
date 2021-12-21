package com.my_web.community.community_demo.DAO;

import com.my_web.community.community_demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

@Mapper
public interface User_Mapper {

    User selectById(int id);

    User selectByName(String name);

    User selectByEmail(String email);

    //-----------------------------***************-----------------------//
    int insertUser(User user);

    int insertUser1(String username, Date createTime);

    void updateStatus(int id, int status);

    void updateHeaderUrl(int id, String headerUrl);

    void updatePassword(int id, String password);

}
