package com.my_web.community.community_demo.entity;

public interface Activation_result {
    int Activation_success = 0;
    int Activation_failed = 1;
    int Activation_repeat = 2;

    // 登陆凭证的有效时间
    int Default_expired_time = 3600 * 12;

    int Remembered_expired_time = 3600 * 24 * 90;
}
