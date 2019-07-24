package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 监听器的作用:
 *  * 1.获取消息
 *  * 2.获取消息的内容 转换数据
 *  * 3.更新索引库
 */
public class GoodsMessageListener implements MessageListenerConcurrently {
    @Autowired
    private ItemSearchService dao;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println(">>>接受数据");
        try {
            if(list != null) {
                for (MessageExt msg : list) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    switch (messageInfo.getMethod()) {
                        case 1:{  //增加
                            //获取信息
                            String s1 = messageInfo.getContext().toString();
                            List<TbItem> itemList = JSON.parseArray(s1, TbItem.class);
                            dao.updateIndex(itemList);
                            break;
                        }
                        case 2:{  //更新
                            String s1 = messageInfo.getContext().toString();
                            List<TbItem> itemList = JSON.parseArray(s1, TbItem.class);
                            dao.updateIndex(itemList);
                            break;
                        }
                        case 3:{  //删除
                            String s1 = messageInfo.getContext().toString();
                            Long[] longs = JSON.parseObject(s1, Long[].class);
                            dao.deleteByIds(longs);
                            break;
                        }
                        default:
                            break;
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
