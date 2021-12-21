package com.my_web.community.community_demo.util;

import com.my_web.community.community_demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class Hostholder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void cleanUser() {
        users.remove();
    }
}
