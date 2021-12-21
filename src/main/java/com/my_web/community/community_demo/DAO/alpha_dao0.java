package com.my_web.community.community_demo.DAO;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class alpha_dao0 implements dao{
    @Override
    public String dao_return() {
        return "alpha_dao0 return";
    }
}
