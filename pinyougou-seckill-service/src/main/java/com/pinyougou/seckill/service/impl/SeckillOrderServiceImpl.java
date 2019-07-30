package com.pinyougou.seckill.service.impl;

import java.util.Date;
import java.util.List;


import com.pinyougou.SeckillOrderService;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.pojo.SeckillStatus;
import com.pinyougou.seckill.thread.CreateOrderThread;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder> implements SeckillOrderService {


    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
        super(seckillOrderMapper, TbSeckillOrder.class);
        this.seckillOrderMapper = seckillOrderMapper;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (StringUtils.isNotBlank(seckillOrder.getUserId())) {
                criteria.andLike("userId", "%" + seckillOrder.getUserId() + "%");
                //criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getSellerId())) {
                criteria.andLike("sellerId", "%" + seckillOrder.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getStatus())) {
                criteria.andLike("status", "%" + seckillOrder.getStatus() + "%");
                //criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverAddress())) {
                criteria.andLike("receiverAddress", "%" + seckillOrder.getReceiverAddress() + "%");
                //criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiverMobile())) {
                criteria.andLike("receiverMobile", "%" + seckillOrder.getReceiverMobile() + "%");
                //criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getReceiver())) {
                criteria.andLike("receiver", "%" + seckillOrder.getReceiver() + "%");
                //criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
            }
            if (StringUtils.isNotBlank(seckillOrder.getTransactionId())) {
                criteria.andLike("transactionId", "%" + seckillOrder.getTransactionId() + "%");
                //criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
            }

        }
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 秒杀下单的修改,在下单的时候，调用多线程下单的方法
     * @param id
     * @param userId
     */

    @Autowired
    private CreateOrderThread createOrderThread;  //注入

    @Override
    public void submitOrder(Long id, String userId) {
        /**
         * 控制用户重复下单，我们在下单之前做一个判定
         */
        if(redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).get(userId) != null) {
            //在队列中
            throw new RuntimeException("已再排队中,有未支付的订单");
        }


        /**
         * 判断是否有未支付的订单
         */
        Object hasOrder = redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if(hasOrder != null) {
            //说明缓存中有订单位支付
            throw new RuntimeException("有未支付的订单");
        }


        //先进行查询数据队列
        Long goodsId = (Long) redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + id).rightPop();
        if(goodsId == null) {
            //说明商品已经没有库存了
            //抛异常
            throw new RuntimeException("商品被抢光");
        }

        //有库存，加入秒杀队列,用户加入队列中
        redisTemplate.boundListOps(SysConstants.SEC_KILL_USER_ORDER_LIST).leftPush(
                new SeckillStatus(userId,id,SeckillStatus.SECKILL_queuing));

        //设置排队标识
        redisTemplate.boundHashOps(SysConstants.SEC_USER_QUEUE_FLAG_KEY).put(userId,id);

        createOrderThread.handleOrder();



        /*//1.根据ID 从redis中获取秒杀商品
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(id);

        //2.判断商品是否已经售罄 如果卖完了 抛出异常
       *//* if (tbSeckillGoods == null || tbSeckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("卖完了");
        }*//*

        //3. 减库存,判断库存是否为0  如果卖完了 抛出异常
        tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(id, tbSeckillGoods);

        //4.如果库存为0 更新到数据库中
        if (tbSeckillGoods.getStockCount() <= 0) {  //如果秒杀被抢光
            seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods); //同步到数据库
            //卖光了需要把redis中的缓存清掉
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).delete(id);
        }

        //5.创建一个预订单到redis中
        TbSeckillOrder seckillOrder = new TbSeckillOrder();

        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(tbSeckillGoods.getCostPrice());
        seckillOrder.setSellerId(tbSeckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0");//未支付的状态
        *//**
         * 将构建的订单保存到redis中
         *//*
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).put(userId,seckillOrder);//hash   bigkey  field  value*/

    }

    /**
     * 根据用户id查询订单的对象，查看状态
     * @param userId
     * @return
     */
    @Override
    public TbSeckillOrder getUserOrderStatus(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
    }

    /**
     * 更新订单的状态，支付成功才执行哦
     * @param transaction_id  交易订单号
     * @param userId  支付的用户id
     */
    @Override
    public void updateOrderStatus(String transaction_id, String userId) {
        //获取秒杀订单的信息
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if(seckillOrder != null) {
            //如果存在那就付款了
            seckillOrder.setPayTime(new Date());
            seckillOrder.setStatus("1");
            seckillOrder.setTransactionId(transaction_id);
            //存储到数据库中
            seckillOrderMapper.insert(seckillOrder);

            //删除redis中预订单
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
        }
    }

    /**
     * 支付超时，删除订单返回库存
     * @param userId
     */
    @Override
    public void deleteOrder(String userId) {
        //获取秒杀订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).get(userId);
        if(seckillOrder == null) {
            System.out.println("没有该订单");
            return;
        }
        Long seckillId = seckillOrder.getSeckillId();
        //获取商品对象
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).get(userId);
        if(seckillGoods == null ) { //说明redis中没有了
            //从数据库查询
            seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillId);
            //返会库存量
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            //重新存到Redis中
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);
        }else {
            //恢复库存
            seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(seckillId,seckillGoods);
        }

        //商品队列中恢复元素
        redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+seckillId).leftPush(seckillId);

        //删除预订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER).delete(userId);
    }


}
