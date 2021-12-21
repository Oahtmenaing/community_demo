package com.my_web.community.community_demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class alpha_config {

    @Bean
    public SimpleDateFormat simpletime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
