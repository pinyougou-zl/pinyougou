package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.entity.Cart;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder>  implements OrderService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbItemMapper itemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbPayLogMapper tbPayLogMapper;
	
	private TbOrderMapper orderMapper;

	@Autowired
	public OrderServiceImpl(TbOrderMapper orderMapper) {
		super(orderMapper, TbOrder.class);
		this.orderMapper=orderMapper;
	}

	/**
	 * 重写add方法
	 * @return
	 */
	@Override
	public void add(TbOrder record) {
		//得到购物车数据,从redis中获取
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(record.getUserId());
		List<Long> orderList = new ArrayList<>();  //存放订单号的
		double total_money = 0;  //存钱
		//进行遍历
		for (Cart cart : cartList) {
			long orderId = idWorker.nextId();  //雪花算法算出一个id
			orderList.add(orderId);

			System.out.println("sellerId"+cart.getSellerId());
			TbOrder tbOrder = new TbOrder();  //新创建订单对象
			//设置属性
			tbOrder.setOrderId(orderId);  //订单id
			tbOrder.setUserId(record.getUserId());  //用户名
			tbOrder.setPaymentType(record.getPaymentType()); //支付类型
			tbOrder.setStatus("1");  //状态，默认为付款
			tbOrder.setCreateTime(new Date());  //订单创建日期
			tbOrder.setUpdateTime(new Date());  //订单更新日期
			tbOrder.setReceiverAreaName(record.getReceiverAreaName());  //地址
			tbOrder.setReceiverMobile(record.getReceiverMobile());   //手机号
			tbOrder.setReceiver(record.getReceiver());   //收货人
			tbOrder.setSourceType(record.getSourceType());  //订单来源
			tbOrder.setSellerId(cart.getSellerId());  //商家id

			//循环购物车明细
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);  //商家id
				orderItem.setSellerId(cart.getSellerId());
				TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());
				orderItem.setGoodsId(item.getGoodsId());  //设置商品的spu的id

				//计算金额
				money +=  orderItem.getTotalFee().doubleValue();
				orderItemMapper.insert(orderItem);
			}
			tbOrder.setPayment(new BigDecimal(money));
			total_money += money;
			orderMapper.insert(tbOrder);
		}


		//所有的操作已完成，我们需要统计支付日志（注意，这里是还没有交易付款）
		TbPayLog tbPayLog = new TbPayLog();
		//放入属性
		String outTardeNo = idWorker.nextId()+ "";  //支付单号
		tbPayLog.setOutTradeNo(outTardeNo);  //支付订单号
		tbPayLog.setCreateTime(new Date());  //支付时间
		//订单号列表，逗号分隔
		String ids = orderList.toString().replace("[","").replace("]","").replace("","");
		tbPayLog.setOrderList(ids);  //订单号列表，逗号分隔
		tbPayLog.setPayType("1");  //1为微信支付
		tbPayLog.setTotalFee((long)(total_money*100));  //总价格
		tbPayLog.setTradeState("0");  //支付状态
		tbPayLog.setUserId(record.getUserId());  //用户id
		tbPayLogMapper.insert(tbPayLog);  //插入到日志
		//放入到redis中
		redisTemplate.boundHashOps("payLog").put(record.getUserId(),tbPayLog);  //使用用户id作为key


		//删除redis中的购物数据
		redisTemplate.boundHashOps("cartList").delete(record.getUserId());
	}

	
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
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	/**
	 * 查询日志的方法
	 * @param userId
	 * @return
	 */
	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
	}

	/**
	 * 修改订单的状态
	 * @param out_trade_no
	 * @param transaction_id
	 */
	@Override
	public void updateorderStatus(String out_trade_no, String transaction_id) {
		//1. 修改支付日志状态
		TbPayLog tbPayLog = tbPayLogMapper.selectByPrimaryKey(out_trade_no);
		tbPayLog.setPayTime(new Date());  //交易的时间
		tbPayLog.setTradeState("1");  //改变状态
		tbPayLog.setTransactionId(transaction_id);  //交易号
		tbPayLogMapper.updateByPrimaryKey(tbPayLog);
		//2. 修改关联的订单的状态
		String orderList = tbPayLog.getOrderList();  //获取订单号列表
		String[] split = orderList.split(",");  //获取订单号的数组
		//改变状态
		for (String s : split) {  //取得是订单号
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(s));
			if(tbOrder != null) {
				tbOrder.setStatus("2");
				orderMapper.updateByPrimaryKey(tbOrder);
			}
		}
		//3. 清除缓存中的支付日志对象
		redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
	}
}
