package com.pinyougou.es.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.service.ItemService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class SMSMessageListener implements MessageListenerConcurrently {
    @Resource
    private ItemService itemService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            if (list != null) {
                for (MessageExt msg : list) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    //获取tags数据
                    String tags = msg.getTags();
                    //获取相关信息
                    Long[] ids = JSON.parseObject(s, Long[].class);
                    System.out.println("mssageid===>" + msg.getMsgId() + "====>" + new String(msg.getBody()) + "=====>>" + msg.getTags());

                    if ("EsUpShelf".equals(tags)) {
                        //如果是上架商品
                        itemService.importDataToEs(ids);
                    }
                    if ("EsDownShelf".equals(tags)){
                        //如果是下架商品
                        itemService.deleteDataFromEsByID(ids);

                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
