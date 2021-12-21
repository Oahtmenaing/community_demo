package com.my_web.community.community_demo.DAO;

import org.springframework.stereotype.Repository;

@Repository
public class alpha_dao1 implements dao{
    @Override
    public String dao_return() {
        return "alpha_dao1 return";
    }
}
