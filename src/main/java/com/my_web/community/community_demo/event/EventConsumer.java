package com.my_web.community.community_demo.event;

import com.alibaba.fastjson.JSONObject;
import com.my_web.community.community_demo.entity.Event;
import com.my_web.community.community_demo.entity.Message;
import com.my_web.community.community_demo.service.Message_service;
import com.my_web.community.community_demo.util.Community_Constant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements Community_Constant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private Message_service messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage (ConsumerRecord record) {
        if(record == null) {
            logger.error("消息内容为空");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event==null) {
            logger.error("消息格式错误");
            return;
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry: event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
