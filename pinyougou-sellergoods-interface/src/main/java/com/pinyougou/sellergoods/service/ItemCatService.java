package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbItemCat;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService extends CoreService<TbItemCat> {
	/**
	 * 增加方法
	 * @return
	 */
	List<TbItemCat> findByParentId(Long parentId);
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat ItemCat);

	void updateStatus(Long[] ids, String status);


	PageInfo<TbItemCat> oneFindPage(Integer pageNo, Integer pageSize, TbItemCat itemCat);

	List<TbItemCat> oneFindByParentId(Long parentId);
}
