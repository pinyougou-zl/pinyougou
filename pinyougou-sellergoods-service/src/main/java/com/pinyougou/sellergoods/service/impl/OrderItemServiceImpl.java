package com.pinyougou.sellergoods.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import com.entity.OrderItemSale;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbOrderItem;

import com.pinyougou.sellergoods.service.OrderItemService;

import javax.annotation.Resource;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderItemServiceImpl extends CoreServiceImpl<TbOrderItem> implements OrderItemService {


    private TbOrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemServiceImpl(TbOrderItemMapper orderItemMapper) {
        super(orderItemMapper, TbOrderItem.class);
        this.orderItemMapper = orderItemMapper;
    }


    @Override
    public PageInfo<TbOrderItem> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbOrderItem> all = orderItemMapper.selectAll();
        PageInfo<TbOrderItem> info = new PageInfo<TbOrderItem>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrderItem> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbOrderItem> findPage(Integer pageNo, Integer pageSize, TbOrderItem orderItem) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbOrderItem.class);
        Example.Criteria criteria = example.createCriteria();

        if (orderItem != null) {
            if (StringUtils.isNotBlank(orderItem.getTitle())) {
                criteria.andLike("title", "%" + orderItem.getTitle() + "%");
                //criteria.andTitleLike("%"+orderItem.getTitle()+"%");
            }
            if (StringUtils.isNotBlank(orderItem.getPicPath())) {
                criteria.andLike("picPath", "%" + orderItem.getPicPath() + "%");
                //criteria.andPicPathLike("%"+orderItem.getPicPath()+"%");
            }
            if (StringUtils.isNotBlank(orderItem.getSellerId())) {
                criteria.andLike("sellerId", "%" + orderItem.getSellerId() + "%");
                //criteria.andSellerIdLike("%"+orderItem.getSellerId()+"%");
            }

        }
        List<TbOrderItem> all = orderItemMapper.selectByExample(example);
        PageInfo<TbOrderItem> info = new PageInfo<TbOrderItem>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrderItem> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Resource
    private TbOrderMapper orderMapper;

    @Override
    public List<OrderItemSale> searchOrderByCreateTime(Integer pageNo, Integer pageSize, Long startTime, Long endTime) {
//        PageHelper.startPage(pageNo, pageSize);
        //按支付订单的创建时间查询某时间段内的订单
        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        //时间戳转换为本地时间
//        Date dateTime = new Date(Long.valueOf(startTime + "000"));
//        Date dateTime1 = new Date(Long.valueOf(endTime + "000"));
        Timestamp timestamp = new Timestamp(Long.valueOf(startTime + "000"));
        Timestamp timestamp1 = new Timestamp(Long.valueOf(endTime + "000"));
        System.out.println("开始时间："+timestamp+"，结束时间："+timestamp1);
        //设置查询条件
        criteria.andEqualTo("status", 1);
        criteria.andGreaterThanOrEqualTo("createTime", timestamp);
        criteria.andLessThanOrEqualTo("createTime", timestamp1);

        List<TbOrder> orderList = orderMapper.selectByExample(example);
        String s = JSON.toJSONString(orderList);
        System.out.println(s);


        //创建OrderSales集合，用来存储获得的OrderSale对象
        List<TbOrderItem> tbOrderItemList = new ArrayList<TbOrderItem>();
        //遍历查询orderList
        for (TbOrder tbOrder : orderList) {
            //获取对应的orderId
            Long orderId = tbOrder.getOrderId();
            Example example1 = new Example(TbOrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId", orderId);

            //根据orderId查询orderItem，返回orderItem集合
            List<TbOrderItem> tbOrderItemList1 = orderItemMapper.selectByExample(example1);
            tbOrderItemList.addAll(tbOrderItemList1);
        }

        String s2 = JSON.toJSONString(tbOrderItemList);
        System.out.println(s2);

        //新建集合，存放orderItem的id作为key，orderItem作为value，进行去重
        Map<Long, TbOrderItem> itemIdMap = new HashMap<>();
        for (TbOrderItem tbOrderItem : tbOrderItemList) {
            Long itemId = tbOrderItem.getItemId();
            System.out.println(itemId);
            itemIdMap.put(itemId, tbOrderItem);
        }

        List<OrderItemSale> itemSaleList = new ArrayList<>();

        Set<Map.Entry<Long, TbOrderItem>> entries = itemIdMap.entrySet();
        for (Map.Entry<Long, TbOrderItem> entry : entries) {
            TbOrderItem tbOrderItem = entry.getValue();
            OrderItemSale orderItemSale = new OrderItemSale();
            orderItemSale.setItemId(tbOrderItem.getItemId());
            orderItemSale.setGoodsId(tbOrderItem.getGoodsId());
            orderItemSale.setTitle(tbOrderItem.getTitle());
            orderItemSale.setPrice(tbOrderItem.getPrice());
            orderItemSale.setPicPath(tbOrderItem.getPicPath());
            orderItemSale.setSellerId(tbOrderItem.getSellerId());

            double money = 0;
            long num = 0;
            Long key = entry.getKey();
            for (TbOrderItem OrderItem : tbOrderItemList) {
                if (key == OrderItem.getItemId()) {
                    //计算金额
                    money += OrderItem.getTotalFee().doubleValue();
                    num += OrderItem.getNum();
                }
            }
            orderItemSale.setNum(num);
            orderItemSale.setTotalFee(BigDecimal.valueOf(money));

            itemSaleList.add(orderItemSale);
        }

        Collections.sort(itemSaleList, new Comparator<OrderItemSale>() {
            @Override
            public int compare(OrderItemSale o1, OrderItemSale o2) {
                return o2.getTotalFee().compareTo(o1.getTotalFee());
            }
        });

        String s1 = JSON.toJSONString(itemSaleList);
        System.out.println(s1);


        /*PageInfo<TbOrderItem> info = new PageInfo<TbOrderItem>(tbOrderItemList);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrderItem> pageInfo = JSON.parseObject(s, PageInfo.class);*/

        return itemSaleList;
    }

}
