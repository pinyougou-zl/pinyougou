package com.pinyougou.seckill.task;

import com.pinyougou.common.util.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 任务类，作用每30秒重新从数据库中查询所有商品的秒杀商品，将其存到redis中
 */

@Component
public class GoodsTask {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    //每隔30秒执行一次
    @Scheduled(cron = "0/5 * * * * ?")
    public void pushGoods() {
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");  //审核通过的
        criteria.andGreaterThan("stockCount",0);  //库存大于0的
        Date date = new Date();
        criteria.andLessThan("startTime",date);  //小于
        criteria.andGreaterThan("endTime",date);  //大于

        //排除已在redis的商品
        Set<Long> keys = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).keys();
        if(keys != null && keys.size()>0) {
            criteria.andNotIn("id",keys);
        }
        List<TbSeckillGoods> goods = seckillGoodsMapper.selectByExample(example);
        //全部存到redis中
        for (TbSeckillGoods good : goods) {
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS).put(good.getId(),good);
            pushGoodsList(good);
        }
    }


    //写一个方法
    public void pushGoodsList(TbSeckillGoods goods) {
        //向同一个队列压入商品数据
        //其实说白了，类似数组，他的长度也就是他的库存数量
        for (Integer i = 0; i < goods.getStockCount(); i++) {
            //这个是redis中的队列数据
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+goods.getId()).leftPush(goods.getId());
        }
    }
}
