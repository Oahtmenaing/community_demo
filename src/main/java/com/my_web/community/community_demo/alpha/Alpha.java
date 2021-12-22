package com.my_web.community.community_demo.alpha;

import com.my_web.community.community_demo.DAO.Discusspost_Mapper;
import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.entity.DiscussPost;
import com.my_web.community.community_demo.entity.User;
import com.my_web.community.community_demo.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class Alpha {

    @Autowired
    User_Mapper user_mapper;

    @Autowired
    CommunityUtil communityUtil;

    @Autowired
    Discusspost_Mapper discusspost_mapper;

    @RequestMapping(path = "/alpha/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String cookie_get(HttpServletResponse response) {
        Cookie cookie = new Cookie("test_cookie", communityUtil.createRandomId());
        cookie.setPath("/community_demo/alpha");
        cookie.setMaxAge(60 * 10);
        response.addCookie(cookie);
        return "get cookie";
    }

    @RequestMapping(path = "/alpha/cookie/back", method = RequestMethod.GET)
    @ResponseBody
    public String cookie_back(@CookieValue("test_cookie") String cookie_code) {
        System.out.println(cookie_code);
        return "back cookie";
    }

    @RequestMapping(path = "/alpha/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String session_set(HttpSession session){
        session.setAttribute("name", "test_session");
        session.setAttribute("value", "5");
        return "set session";
    }

    @RequestMapping(path = "/alpha/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String session_get(HttpSession session){
        System.out.println(session.getAttribute("name"));
        System.out.println(session.getAttribute("value"));
        return "get session";
    }

    //@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public String save1() {
        User user = new User();
        user.setUsername("Alpha");
        user.setPassword("123");
        user.setEmail("oathmeaning@gmail.com");
        user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        user_mapper.insertUser(user);

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle("123");
        post.setContent("123");
        post.setCreateTime(new Date());
        discusspost_mapper.insert_discuss_post(post);

        Integer.valueOf("abc");

        return "OK";
    }

    @Autowired
    TransactionTemplate transactionTemplate;

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus status) {
                User user = new User();
                user.setUsername("Alpha");
                user.setPassword("123");
                user.setEmail("oathmeaning@gmail.com");
                user.setHeaderUrl("http://image.nowcoder.com/head/99t.png");
                user.setCreateTime(new Date());
                user_mapper.insertUser(user);

                DiscussPost post = new DiscussPost();
                post.setUserId(user.getId());
                post.setTitle("123");
                post.setContent("123");
                post.setCreateTime(new Date());
                discusspost_mapper.insert_discuss_post(post);

                Integer.valueOf("abc");
                return "OK";
            }
        });
    }

}
