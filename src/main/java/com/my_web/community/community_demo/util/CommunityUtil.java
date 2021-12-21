package com.my_web.community.community_demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

@Component
public class CommunityUtil {

    //生成随机字符串
    public String createRandomId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    //MD5加密
    public String MD5_Transer(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getJSONString(int id, String message, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", id);
        json.put("message", message);
        if (map != null){
            for (String key: map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int id, String message) {
        return getJSONString(id, message, null);
    }

    public static String getJSONString(int id) {
        return getJSONString(id, null, null);
    }
}
