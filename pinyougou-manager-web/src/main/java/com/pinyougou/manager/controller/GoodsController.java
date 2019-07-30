package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.entity.Goods;
import com.entity.Result;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
	@Autowired
	private DefaultMQProducer producer;

	@Reference
	private ItemPageService itemPageService;

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemSearchService itemSearchService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取名称
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getTbGoods().setSellerId(name);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			MessageInfo messageInfo = new MessageInfo("Goods_Topic","goods_delete_tag","delete",ids,MessageInfo.METHOD_DELETE);

			producer.send(new Message(messageInfo.getTopic(),messageInfo.getTags(),messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));

			//删除完之后，需要进行删除索引库
			//itemSearchService.deleteByIds(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {
		//由于先在该身份是审核的，所以列表都要查出来，不需要筛选
		//goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        return goodsService.findPage(pageNo, pageSize, goods);
    }

    //添加方法，对商品的审核
	@RequestMapping("/updateStatus/{status}")
	public Result updateStatus(@RequestBody Long[] ids,@PathVariable String status) {
		try {
			goodsService.updateStatus(ids,status);
			if("1".equals(status)) {
				//根据Spu的商品id获取SKU列表
				List<TbItem> tbItemListByIds = goodsService.findTbItemListByIds(ids);

				//放入MQserver
				//进行封装
				MessageInfo messageInfo = new MessageInfo(
				"Goods_Topic","goods_update_tag","updateStatus",tbItemListByIds,MessageInfo.METHOD_UPDATE
				);
				//消息发送 JSON.toJSONString(messageInfo)转换成字符串
				SendResult send = producer.send(new Message(messageInfo.getTopic(), messageInfo.getTags()
						, messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes()));
				System.out.println(">>>>"+send.getSendStatus());

				//使用MQ不在控制层直接进行修改
				/*//数据更新完之后，进行更新索引（更新es）
				itemSearchService.updateIndex(tbItemListByIds);
				//进行生成静态页面
				for (Long id : ids) {
					//生成静态页
					itemPageService.genItemHtml(id);
					System.out.println("生成完毕");
				}*/
			}
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
	
}
