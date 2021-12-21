package com.my_web.community.community_demo;

import com.my_web.community.community_demo.DAO.User_Mapper;
import com.my_web.community.community_demo.DAO.dao;
import com.my_web.community.community_demo.DAO.alpha_dao1;
import com.my_web.community.community_demo.DAO.no_dao_test;
import com.my_web.community.community_demo.alpha.Alpha;
import com.my_web.community.community_demo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityDemoApplication.class)
class CommunityDemoApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Autowired
	private Alpha alpha;


	//使用getbean进行依赖注入（主动注入）
	@Test
	void contextLoads() {
		System.out.println(applicationContext);
		dao  alpha_dao0 = applicationContext.getBean(dao.class);
		dao alpha_dao = applicationContext.getBean("dao1", dao.class);
		System.out.println((alpha_dao0.dao_return()));
		System.out.println((alpha_dao.dao_return()));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void no_dao_test_object() {
		no_dao_test ndt0 = applicationContext.getBean(no_dao_test.class);
		System.out.println(ndt0);

		ndt0 = applicationContext.getBean(no_dao_test.class);
		System.out.println(ndt0);
	}

	@Test
	public void cofig_test() {
		SimpleDateFormat sdf = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(sdf.format(new Date()));
	}

	@Autowired
	private User_Mapper user_Mapper;

	@Test
	public void selectByID_test() {
		User user = user_Mapper.selectById(101);
		System.out.println(user);
	}

	@Test
	public void alpha_test1() {
		Object obj = alpha.save1();
		System.out.println(obj);
	}

	@Test
	public void alpha_test2() {
		Object obj = alpha.save2();
		System.out.println(obj);
	}
}
