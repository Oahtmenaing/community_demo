package com.my_web.community.community_demo;

import com.my_web.community.community_demo.DAO.Discusspost_Mapper;
import com.my_web.community.community_demo.DAO.Login_ticket_Mapper;
import com.my_web.community.community_demo.DAO.Message_Mapper;
import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.alpha.Alpha;
import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.entity.Login_ticket;
import com.my_web.community.community_demo.entity.Message;
import com.my_web.community.community_demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityDemoApplication.class)
public class Dao_test {

    @Autowired
    private Alpha alpha;

    @Autowired
    private User_Mapper user_Mapper;

    @Autowired
    private Login_ticket_Mapper login_ticket_mapper;

    @Test
    public void selectByID_test() {
        User user = user_Mapper.selectById(164);
        System.out.println(user);
    }

    @Test
    public void selectByName_test() {
        User user = user_Mapper.selectByName("liubei");
        System.out.println(user);
    }

    @Test
    public void selectByEmail_test() {
        User user = user_Mapper.selectByEmail("nowcoder102@sina.com");
        System.out.println(user);
    }

    @Test
    public void insertUser_test() {
        User new_user = new User();
        new_user.setUsername("Oathmeaning");
        new_user.setPassword("doctor123");
        new_user.setEmail("oathmeaning@gmail.com");
        new_user.setSalt("333");
        new_user.setHeaderUrl("www.oathmeaning.com");
        new_user.setCreateTime(new Date());
        System.out.println(new_user);
        user_Mapper.insertUser(new_user);

        User user = user_Mapper.selectByEmail("oathmeaning@gmail.com");
        System.out.println(user);
    }

    @Test
    public void updatePassword_test() {
        user_Mapper.updatePassword(154, "doctor1_2_3");

        User user = user_Mapper.selectByEmail("oathmeaning@gmail.com");
        System.out.println(user);
    }

    @Test
    public void insertUser1() {
        user_Mapper.insertUser1("Oathmean", new Date());

        User user = user_Mapper.selectByEmail("Oathmean");
        System.out.println(user);
    }

    @Autowired
    Discusspost_Mapper discusspost_mapper;

    @Test
    public void select_discusspost() {
        List<DiscussPost> discussPostList = discusspost_mapper.select_discuss_post(0, 0, 5);
        for(DiscussPost post: discussPostList)
            System.out.println(post);
        int discussPostList_count = discusspost_mapper.count_discuss_post(0);
        System.out.println(discussPostList_count);
    }

    @Test
    public void insert_ticket_test() {
        Login_ticket login_ticket = new Login_ticket();
        login_ticket.setExpired(new Date());
        login_ticket.setUserId(123);
        login_ticket.setTicket("12345");
        login_ticket.setStatus(0);
        login_ticket_mapper.insert_login_ticket(login_ticket);
    }

    @Test
    public void update_ticket_test() {
        String ticket = "12345";
        login_ticket_mapper.update_status(ticket, 1);
        Login_ticket login_ticket = login_ticket_mapper.selectByticket(ticket);
        System.out.println(login_ticket);
    }

    @Test
    public void alpha_test() {
        alpha.save1();
    }

    @Autowired
    Message_Mapper message_mapper;

    @Test
    public void message_test() {
        List<Message> messageList = message_mapper.selectConversation(111, 0, 20);
        for (Message message: messageList){
            System.out.println(message);
        }
        System.out.println(message_mapper.selectConversationCount(111));
        List<Message> letterList = message_mapper.selectLetterByConversation("111_112", 0, 20);
        for (Message letter: letterList){
            System.out.println(letter);
        }
        System.out.println(message_mapper.selectLetterCountByConversation("111_112"));
        System.out.println(message_mapper.selectUnreadLetterCount(131, "111_131"));
    }
}
