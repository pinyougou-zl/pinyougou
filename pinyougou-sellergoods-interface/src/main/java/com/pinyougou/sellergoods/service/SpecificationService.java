package com.pinyougou.sellergoods.service;
import com.entity.Specification;
import com.pinyougou.pojo.TbSpecification;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {

	//增加规格选项的方法
	void add(Specification specification);
	//进行回显的方法
	Specification findOne(Long id);
	//对修改的方法进行修改
	void update(Specification specification);
	//删除的方法修改
	void delete(Long[] ids);

	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);


	void updateApply(Specification specification);

	PageInfo<TbSpecification> oneFindPage(Integer pageNo, Integer pageSize, TbSpecification specification);

	//获取数据回显
	Specification oneFindOne(Long id);
}
