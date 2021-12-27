package com.my_web.community.community_demo.event;

import com.alibaba.fastjson.JSONObject;
import com.my_web.community.community_demo.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件
    public void fireEvent (Event event) {
        // 将时间发布至主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
