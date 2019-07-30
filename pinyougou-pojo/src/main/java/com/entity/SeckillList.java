package com.entity;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import java.io.Serializable;

public class SeckillList implements Serializable {
    private TbSeckillGoods seckillGoods;
    private TbSeckillOrder seckillOrder;

    public SeckillList(TbSeckillGoods seckillGoods, TbSeckillOrder seckillOrder) {
        this.seckillGoods = seckillGoods;
        this.seckillOrder = seckillOrder;
    }

    public SeckillList() {
    }

    public TbSeckillGoods getSeckillGoods() {
        return seckillGoods;
    }

    public void setSeckillGoods(TbSeckillGoods seckillGoods) {
        this.seckillGoods = seckillGoods;
    }

    @Override
    public String toString() {
        return "SeckillList{" +
                "seckillGoods=" + seckillGoods +
                ", seckillOrder=" + seckillOrder +
                '}';
    }

    public TbSeckillOrder getSeckillOrder() {
        return seckillOrder;
    }

    public void setSeckillOrder(TbSeckillOrder seckillOrder) {
        this.seckillOrder = seckillOrder;
    }
}
