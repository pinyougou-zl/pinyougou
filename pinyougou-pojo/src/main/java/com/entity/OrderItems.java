package com.entity;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

//订单详情列表pojo
public class OrderItems implements Serializable {
    /*参数：订单id：order_id，商品购买数量:num,商品标题:title,实付金额:payment
		订单创建时间:create_time,交易关闭时间:end_time,	*/
    private Long orderId;            //tbOrderItem
    private String title;            //tbOrderItem
    private Integer num;              //tbOrderItem
    private BigDecimal price;         // tbOrderItem

    private BigDecimal payment;       //tbOrder
    private Date createTime;          //tbOrder
    private Date endTime;             //tbOrder
    private String receiver;           //tbOrder

    @Override
    public String toString() {
        return "OrderItems{" +
                "orderId=" + orderId +
                ", title='" + title + '\'' +
                ", num=" + num +
                ", price=" + price +
                ", payment=" + payment +
                ", createTime=" + createTime +
                ", endTime=" + endTime +
                ", receiver='" + receiver + '\'' +
                '}';
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


}
