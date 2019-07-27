package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageMessageListener implements MessageListenerConcurrently {
    @Resource
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println(">>>>当前的线程>>>>" + Thread.currentThread().getName());
        try {
            if(list != null) {
                for (MessageExt msg : list) {
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    switch (messageInfo.getMethod()) {
                        case MessageInfo.METHOD_ADD: //新增
                        {

                            break;
                        }
                        case MessageInfo.METHOD_UPDATE: //更新
                        {
                            Object context = messageInfo.getContext(); //[{},{},{}]
                            String s1 = context.toString();
                            List<TbItem> itemList = JSON.parseArray(s1, TbItem.class);
                            Set<Long> set = new HashSet<>();
                            for (TbItem item : itemList) {
                                Long goodsId = item.getGoodsId();
                                set.add(goodsId);
                            }
                            for (Long o : set) {
                                itemPageService.genItemHtml(o);
                            }
                            break;
                        }
                        case MessageInfo.METHOD_DELETE: //删除
                        {
                            Object con = messageInfo.getContext();
                            String s1 = con.toString();
                            Long[] list1 = JSON.parseObject(s1, Long[].class);
                            itemPageService.deleteById(list1);
                            break;
                        }
                        default:
                            break;
                    }
                }
            }
            //返回成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}
