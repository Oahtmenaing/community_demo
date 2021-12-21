package com.my_web.community.community_demo.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Scope("prototype")
public class no_dao_test {
    public no_dao_test() {
        System.out.println("实例化 no_dao_test");
    }

    @PostConstruct
    public void object0() {
        System.out.println("初始化 No_dao_test");
    }

    @PreDestroy
    public void object2() {
        System.out.println("销毁 No_dao_test");
    }

    @Autowired
    @Qualifier("alpha_dao1")
    private dao dao1;
    public String find() {
        return dao1.dao_return();
    }
}
