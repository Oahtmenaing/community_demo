package com.my_web.community.community_demo.DAO;

import com.my_web.community.community_demo.entity.Login_ticket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface Login_ticket_Mapper {

    @Insert({"insert into login_ticket(user_id,ticket,status,expired) values(#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert_login_ticket(Login_ticket login_ticket);

    @Select({"select id, user_id, ticket, status, expired from login_ticket where ticket=#{ticket}"})
    Login_ticket selectByticket(String ticket);

    @Update({"update login_ticket set status=#{status} where ticket=#{ticket}"})
    int update_status(String ticket, int status);
}
