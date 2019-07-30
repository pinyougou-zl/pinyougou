package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.Result;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;


	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){
		return userService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user){
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public TbUser findOne(@PathVariable(value = "id") Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                     @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }


	/**
	 * 用户统计功能
	 * @return
	 * 作者：房靖滔
	 */
	@RequestMapping("/userCount")
	public Map<String,Object> userCount(){
		//统计数据，获取最受用户喜欢的商品
		List<TbGoods> goodsList = userService.userCount();
		//转成Map集合
		Map<String,Object> map = new HashMap<>();
		List<String> goodsName = new ArrayList<>();
		List<Long> sellerNumber = new ArrayList<>();
		for (TbGoods tbGoods : goodsList) {
			goodsName.add(tbGoods.getGoodsName());
			sellerNumber.add(tbGoods.getSellerNumber());
		}
		map.put("goodsName", goodsName);
		map.put("sellerNumber", sellerNumber);
		return map;
	}


	/**
	 * 统计活跃用户和非活跃用户
	 * @return
	 * 作者：房靖滔
	 */
	@RequestMapping("/userActive")
	public Map<String,Object> userActive(){
		Map<String, Object> map = userService.userActive();
		return map;
	}

	/**
	 * 使用POI导出用户数据
	 */
	@RequestMapping("/userExport")
	public Result userExport(){
		try {
			List<TbUser> allUser = userService.findAll();
			userService.userExport(allUser);
			return new Result(true, "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "导出失败");
		}
	}
	
}
