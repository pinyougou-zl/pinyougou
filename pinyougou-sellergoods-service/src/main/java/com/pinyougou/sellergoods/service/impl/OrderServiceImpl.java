package com.pinyougou.sellergoods.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.entity.OrderItems;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder>  implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	public OrderServiceImpl(TbOrderMapper orderMapper) {
		super(orderMapper, TbOrder.class);
		this.orderMapper=orderMapper;
	}
 @Autowired
	private TbOrderItemMapper orderItemMapper;
	

	
	@Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(order!=null){			
						if(StringUtils.isNotBlank(order.getPaymentType())){
				criteria.andLike("paymentType","%"+order.getPaymentType()+"%");
				//criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(StringUtils.isNotBlank(order.getPostFee())){
				criteria.andLike("postFee","%"+order.getPostFee()+"%");
				//criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(StringUtils.isNotBlank(order.getStatus())){
				criteria.andLike("status","%"+order.getStatus()+"%");
				//criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingName())){
				criteria.andLike("shippingName","%"+order.getShippingName()+"%");
				//criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingCode())){
				criteria.andLike("shippingCode","%"+order.getShippingCode()+"%");
				//criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getUserId())){
				criteria.andLike("userId","%"+order.getUserId()+"%");
				//criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerMessage())){
				criteria.andLike("buyerMessage","%"+order.getBuyerMessage()+"%");
				//criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerNick())){
				criteria.andLike("buyerNick","%"+order.getBuyerNick()+"%");
				//criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerRate())){
				criteria.andLike("buyerRate","%"+order.getBuyerRate()+"%");
				//criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverAreaName())){
				criteria.andLike("receiverAreaName","%"+order.getReceiverAreaName()+"%");
				//criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+order.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverZipCode())){
				criteria.andLike("receiverZipCode","%"+order.getReceiverZipCode()+"%");
				//criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiver())){
				criteria.andLike("receiver","%"+order.getReceiver()+"%");
				//criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(order.getInvoiceType())){
				criteria.andLike("invoiceType","%"+order.getInvoiceType()+"%");
				//criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSourceType())){
				criteria.andLike("sourceType","%"+order.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSellerId())){
				criteria.andLike("sellerId","%"+order.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
        List<TbOrder> all = orderMapper.selectByExample(example);
		 for (TbOrder tbOrder : all) {
			 System.out.println(tbOrder.getPaymentTime());
		 }
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


	//全部订单查询
	@Override
	public List<OrderItems> findOrderItems() {
		Example example = new Example(TbOrder.class);
		Example.Criteria criteria = example.createCriteria();

		Example exampleTbOrderItem = new Example(TbOrderItem.class);
		Example.Criteria criteriaTbOrderItem = exampleTbOrderItem.createCriteria();

		//List<TbOrder> all = orderMapper.selectByExample(example);

		//List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(criteriaTbOrderItem);

		List<TbOrderItem> tbOrderItems = orderItemMapper.selectAll();
		//创建Oders组合类集合，存储TbOrder和TbOrderItem值
		List<OrderItems> ordersList = new ArrayList<OrderItems>();
		//创建Oders组合类，存储TbOrder和TbOrderItem值
		//TbOrder tbOrder = new TbOrder();
		//遍历所有TbOrderItem对象集合元素

		for (TbOrderItem tbOrderItem : tbOrderItems) {
			OrderItems orderItems = new OrderItems();
			//if (tbOrderItem!=null) {
			//设置TbOrderItem属性
			orderItems.setOrderId(tbOrderItem.getOrderId());
			orderItems.setTitle(tbOrderItem.getTitle());
			orderItems.setPrice(tbOrderItem.getPrice());
			orderItems.setNum(tbOrderItem.getNum());
			Long orderId = tbOrderItem.getOrderId();

			if (orderId<=10) {
				TbOrder tbOrder = orderMapper.selectByPrimaryKey(orderId);

				Date createTime = tbOrder.getCreateTime();
				String receiver = tbOrder.getReceiver();
				orderItems.setCreateTime(tbOrder.getCreateTime());
				Date endTime = tbOrder.getEndTime();
				orderItems.setEndTime(endTime);

				orderItems.setReceiver(tbOrder.getReceiver());
				orderItems.setPayment(tbOrder.getPayment());


				//List<TbOrder> tbOrders = orderMapper.selectByExample(example);
				//example.createCriteria().andIn(createTime.toString(), createTime)
				/*TbOrder tbOrder1 = orderMapper.selectByPrimaryKey(example);
				System.out.print("商品名："+tbOrderItem.getTitle() + "===");
				System.out.println("销售额："+tbOrder1.getPayment());*/

				ordersList.add(orderItems);
			}
		}

		System.out.println("ordersList值=========" + ordersList);
		return ordersList;
	}

	//全部商品销售统计
	@Override
	public BigDecimal findGoodsSellerPayment() {
		Example example = new Example(TbOrder.class);
		Example.Criteria criteria = example.createCriteria();

		Example exampleTbOrderItem = new Example(TbOrderItem.class);
		Example.Criteria criteriaTbOrderItem = exampleTbOrderItem.createCriteria();
		//创建TbOrderItem集合
		List<TbOrderItem> tbOrderItemList = orderItemMapper.selectByExample(exampleTbOrderItem);
		List<OrderItems> orderItemsList=new ArrayList<>();


		BigDecimal payment=new BigDecimal("0");
		Date createTime=new Date();
			//遍历tbOrderItem对象集合
			for (TbOrderItem tbOrderItem : tbOrderItemList) {
				OrderItems orderItems = new OrderItems();
				Long orderId = tbOrderItem.getOrderId();
				orderItems.setOrderId(orderId);
				orderItems.setTitle(tbOrderItem.getTitle());
				orderItems.setPrice(tbOrderItem.getPrice());
				orderItems.setNum(tbOrderItem.getNum());

				example.createCriteria().andEqualTo("orderId", tbOrderItem.getOrderId());
				List<TbOrder> tbOrders = orderMapper.selectByExample(example);
				for (TbOrder tbOrder : tbOrders) {
					createTime = tbOrder.getCreateTime();
					Date endTime = tbOrder.getEndTime();

					//设定时间段查询
					example.createCriteria().andGreaterThanOrEqualTo("createTime", createTime);
					example.createCriteria().andLessThanOrEqualTo("endTime",endTime);

					List<TbOrder> tbOrders1 = orderMapper.selectByExample(example);
					for (TbOrder order : tbOrders1) {
						//if (order.getOrderId()>=3) {


						BigDecimal payment1 = order.getPayment();
						payment = payment.add(payment1);


						orderItems.setPayment(order.getPayment());
						orderItems.setReceiver(order.getReceiver());

						orderItems.setPayment(order.getPayment());
						System.out.println("销售额："+payment);
					//}
					}
					System.out.print("商品名："+createTime + "===");
				}
			}


		return payment;
	}




}
