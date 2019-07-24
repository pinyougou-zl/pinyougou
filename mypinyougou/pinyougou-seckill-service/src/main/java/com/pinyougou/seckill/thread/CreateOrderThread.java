package com.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.pojo.SeckillStatus;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Date;

/**
 * 这个是用于执行多线程的方法，使用@Async注解来声明这是一个异步任务，由线程池来调度
 */
public class CreateOrderThread {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    //多线程执行下单操作 异步方法
    @Async
    public void handleOrder() {
        try {
            //这个方法是获取线程的名字
            System.out.println("模拟处理订单开始========"+Thread.currentThread().getName());
            //进行睡眠10s
            Thread.sleep(10000);
            System.out.println("模拟处理订单结束 总共耗费10秒钟======="+Thread.currentThread().getName());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //获取队列列表中的信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).rightPop();
        if(seckillStatus != null) {
            //有，从redis中获取商品
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());
            //将这个商品的库存减少
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
            //重新存入
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(),tbSeckillGoods);


            if(tbSeckillGoods.getStockCount() <= 0) {
                //如果被抢光,同步到数据库
                seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
                //缓存清空
                redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());
            }

            //创建订单
            TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
            tbSeckillOrder.setId(idWorker.nextId());  //这个就是订单id
            tbSeckillOrder.setCreateTime(new Date());
            tbSeckillOrder.setMoney(tbSeckillGoods.getCostPrice());  //秒杀价格
            tbSeckillOrder.setSeckillId(tbSeckillGoods.getGoodsId());  //商品id
            tbSeckillOrder.setSellerId(tbSeckillGoods.getSellerId()); //商家id
            tbSeckillOrder.setUserId(seckillStatus.getUserId()); //用户id
            tbSeckillOrder.setStatus("0");  //未支付

            //将构建的订单保存到redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(seckillStatus.getUserId(),tbSeckillOrder);

            //移除排队标识，标识下单成功
            redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

            //立即发送延时消息
            sendMessage(tbSeckillOrder);
        }
    }

    private void sendMessage(TbSeckillOrder tbSeckillOrder) {
        try {
            MessageInfo messageInfo = new MessageInfo("TOPIC_SECKILL_DELAY","TAG_SECKILL_DELAY","handleOrder_DELAY",tbSeckillOrder, MessageInfo.METHOD_UPDATE);
            //
            System.out.println("多线程下单============");
            Message message = new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
            //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            //设置消息演示等级 16=30m
            message.setDelayTimeLevel(5);
            defaultMQProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
