package com.my_web.community.community_demo;
import com.my_web.community.community_demo.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityDemoApplication.class)
public class MailTest {

    @Autowired
    MailClient mailClient;

    @Autowired
    TemplateEngine templateEngine;

    //@Test
    public void mailtest() {
        mailClient.sendMail("384449549@qq.com", "Hello!", "Test-Hello");
    }

    //@Test
    public void html_mail_test() {
        Context context = new Context();
        context.setVariable("username", "Oathmeaning");
        String content = templateEngine.process("/mail/demo", context);
        mailClient.sendMail("384449549@qq.com", content, "Test-Hello");
        System.out.println(content);
    }
}
