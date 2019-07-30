package com.entity;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;

public class OrderOne implements Serializable {
    private TbOrderItem orderItem;

    private String goodsName;


    public TbOrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(TbOrderItem orderItem) {
        this.orderItem = orderItem;
    }



    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
