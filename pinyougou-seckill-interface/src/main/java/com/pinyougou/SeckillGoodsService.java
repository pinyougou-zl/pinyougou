package com.pinyougou;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSeckillGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService extends CoreService<TbSeckillGoods> {
	
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillGoods> findPage(Integer pageNo, Integer pageSize, TbSeckillGoods SeckillGoods);

    void updateStatus(String status, Long[] ids);


	/**
	 * 根据id查询goods信息，然后根据里面的时间把倒计时写活
	 * @param id
	 * @return
	 */
	Map getGoodsById(Long id);
}
