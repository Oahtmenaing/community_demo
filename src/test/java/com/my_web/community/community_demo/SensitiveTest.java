package com.my_web.community.community_demo;


import com.my_web.community.community_demo.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityDemoApplication.class)
public class SensitiveTest {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text = "这里可以⭐开⭐票⭐，可以⭐吸⭐毒⭐，可以⭐嫖⭐娼⭐，可以赌⭐博，好好好！";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
