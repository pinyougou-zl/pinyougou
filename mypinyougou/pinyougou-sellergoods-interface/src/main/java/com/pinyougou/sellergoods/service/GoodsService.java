package com.pinyougou.sellergoods.service;

import com.entity.Goods;
import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbItem;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService extends CoreService<TbGoods> {
	public Goods findOne(Long id);

	public void update(Goods goods);

	//重新写delete方法
	public void delete(Long[] ids);

	//根据商品SPU的数组对象查询所有的该商品的列表数据
	List<TbItem> findTbItemListByIds(Long[] ids);

	/**
	 *
	 * 对商品的审核
	 */
	public void updateStatus(Long[] ids,String status);

	/**
	 * 新增方法，是一个组合对象
	 * @param goods
	 */
	void add(Goods goods);
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);
	
}
