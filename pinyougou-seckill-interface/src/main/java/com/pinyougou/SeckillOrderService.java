package com.pinyougou;
import java.util.List;
import com.pinyougou.pojo.TbSeckillOrder;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);

	/**
	 * 下秒杀订单到redis中
	 * @param id
	 * @param userId
	 */
    void submitOrder(Long id, String userId);

	/**
	 * 查询某一个登录用户的订单对象
	 * @param userId
	 * @return
	 */
	public TbSeckillOrder getUserOrderStatus(String userId);

	/**
	 * 更新订单的状态，支付成功的时候执行
	 * @param transaction_id  交易订单号
	 * @param userId  支付的用户id
	 */
	public void updateOrderStatus(String transaction_id,String userId);

	/**
	 * 支付超时的时候执行，用于删除订单
	 * @param userId
	 */
	public void deleteOrder(String userId);
}
