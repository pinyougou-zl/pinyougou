package com.entity;
import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 包名:com.entity
 * 作者:yang
 * 日期:19-7-29 上午11:21
 */
public class OrderItemSale implements Serializable {
    /**
     * 商品id
     */
    private Long itemId;

    /**
     * SPU_ID
     */
    private Long goodsId;


    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 商品销售数量
     */
    private Long num;

    /**
     * 商品销售总金额
     */
    private BigDecimal totalFee;

    /**
     * 商品图片地址
     */
    private String picPath;

    private String sellerId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
