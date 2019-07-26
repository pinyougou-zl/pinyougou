package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.entity.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.sellergoods.service.GoodsService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl extends CoreServiceImpl<TbGoods>  implements GoodsService {
    @Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbBrandMapper tbBrandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;

	private TbGoodsMapper goodsMapper;

	@Autowired
	public GoodsServiceImpl(TbGoodsMapper goodsMapper) {
		super(goodsMapper, TbGoods.class);
		this.goodsMapper=goodsMapper;
	}

	/**
	 * 跟据前端传来的id进行查询，然后封装
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		//根据主键查询，获取SPU
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);

		//处理itemList，sku的信息
		TbItem item = new TbItem();
		item.setGoodsId(id);
		List<TbItem> itemList = itemMapper.select(item);
		//把三个属性对象都封装到Goods中
		goods.setTbGoods(tbGoods);
		goods.setGoodsDesc(tbGoodsDesc);
		goods.setItemList(itemList);
		return goods;
	}

	/**
	 * 这是修改的方法啊
	 * @param goods
	 */
	@Override
	public void update(Goods goods) {
		//1、获取spu
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0"); //商家修改，新增都需要设置状态为0，需要运营商审核
		goodsMapper.updateByPrimaryKey(tbGoods);
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDescMapper.updateByPrimaryKey(goodsDesc);
		//2、获取sku列表，但是考虑一个问题，需要先删除，再添加
		TbItem item = new TbItem();
		item.setGoodsId(tbGoods.getId());
		itemMapper.delete(item);
		//进行存储
		saveItems(goods,tbGoods,goodsDesc);
	}

	/**
	 * 商品的逻辑删除
	 * @param ids
	 */
	@Override
	public void delete(Long[] ids) {
		//需要进行逻辑删除，并不是物理删除，逻辑删除说白了就是修改该表中的一个字段值
		//sql:update tb_goods set is_delete=1 where id in(ids)
		TbGoods tbGoods = new TbGoods();
		tbGoods.setIsDelete(true);  //true就代表1
		Example example = new Example(TbGoods.class);
		example.createCriteria().andIn("id",Arrays.asList(ids));
		goodsMapper.updateByExampleSelective(tbGoods,example);
	}

	/**
	 * 根据商品SPU的数组对象查询所有的该商品的SKU列表数据
	 * @param ids
	 * @return
	 */
	@Override
	public List<TbItem> findTbItemListByIds(Long[] ids) {
		//设置条件
		Example example = new Example(TbItem.class);
		//商品审核通过，根据goodsid和status查找到item进行同步
		example.createCriteria().andIn("goodsId",Arrays.asList(ids)).andEqualTo("status","1");
		return itemMapper.selectByExample(example);
	}

	/**
	 * 对商品的审核
	 * @param ids
	 * @param status
	 */
	@Override
	public void updateStatus(Long[] ids, String status) {
		TbGoods tbGoods = new TbGoods();
		tbGoods.setAuditStatus(status);  //改变状态
		Example example = new Example(TbGoods.class);
		example.createCriteria().andIn("id", Arrays.asList(ids));
		//update set status=1 where id in(ids)
		goodsMapper.updateByExampleSelective(tbGoods,example);
	}

	/**
	 * 修改的方法，进行新增
	 * @param goods
	 */
	@Override
	public void add(Goods goods) {
		//进行对象属性的补充
		//获取goods
		TbGoods tbGoods = goods.getTbGoods();
		tbGoods.setAuditStatus("0"); //未审核
		tbGoods.setIsDelete(false);  //是否逻辑删除（其实就是没删除，就是更新了而已）
		//添加
		goodsMapper.insert(tbGoods);
		//获取GoodsDesc
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		//这里需要用到GoodsDescMapper,注入
		goodsDescMapper.insert(goodsDesc);

		//TODO
		saveItems(goods,tbGoods,goodsDesc);

		/*//获取skuList列表
		List<TbItem> itemList = goods.getItemList();
		for (TbItem tbItem : itemList) {
			//设置title spu名+空格=规格名称+
			String spec = tbItem.getSpec();//{"网络":"移动4G","机身内存":"16G"}
			String title = tbGoods.getGoodsName();
			Map map = JSON.parseObject(spec,Map.class);
			for (Object key : map.keySet()) {
				String str = (String) map.get(key);
				title += " " + str;
			}
			tbItem.setTitle(title);

			//设置图片地址,从goodsDesc中获取
			List<Map> maps = JSON.parseArray(goodsDesc.getItemImages(),Map.class);
			//获取图片的全名称
			String image = maps.get(0).get("url").toString();
			tbItem.setImage(image);

			//设置分类
			tbItem.setCategoryid(tbGoods.getCategory3Id());
			TbItemCat cat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
			tbItem.setCategory(cat.getName());

			//设置时间
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(tbItem.getCreateTime());

			//设置外键
			tbItem.setGoodsId(tbGoods.getId());

			//设置商家
			tbItem.setSellerId(tbGoods.getSellerId());
			TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
			tbItem.setSeller(tbSeller.getNickName());  //商家的店铺名
			TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			tbItem.setBrand(tbBrand.getName());

			//把所有的数据添加到数据库
			itemMapper.insert(tbItem);
			}*/
	}

	//如果需要自定义规格调用一下方法
	private void saveItems(Goods goods, TbGoods tbGoods, TbGoodsDesc goodsDesc) {
		if("1".equals(tbGoods.getIsEnableSpec())) {  //如果需要自定义
			//获取SKU列表
			//TODO
			//先获取SKU的列表
			List<TbItem> itemList = goods.getItemList();

			for (TbItem tbItem : itemList) {

				//设置title  SPU名 + 空格+ 规格名称 +
				String spec = tbItem.getSpec();//{"网络":"移动4G","机身内存":"16G"}
				String title = tbGoods.getGoodsName();
				Map map = JSON.parseObject(spec, Map.class);
				for (Object key : map.keySet()) {
					String o1 = (String) map.get(key);
					title += " " + o1;
				}
				tbItem.setTitle(title);

				//设置图片从goodsDesc中获取
				//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
				String itemImages = goodsDesc.getItemImages();//

				List<Map> maps = JSON.parseArray(itemImages, Map.class);

				String url = maps.get(0).get("url").toString();//图片的地址
				tbItem.setImage(url);

				//设置分类
				TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
				tbItem.setCategoryid(tbItemCat.getId());
				tbItem.setCategory(tbItemCat.getName());

				//时间
				tbItem.setCreateTime(new Date());
				tbItem.setUpdateTime(new Date());

				//设置SPU的ID
				tbItem.setGoodsId(tbGoods.getId());

				//设置商家
				TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
				tbItem.setSellerId(tbSeller.getSellerId());
				tbItem.setSeller(tbSeller.getNickName());//店铺名

				//设置品牌明后
				TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
				tbItem.setBrand(tbBrand.getName());
				itemMapper.insert(tbItem);
			}
		}else{
			//插入到SKU表 一条记录
			TbItem tbItem = new TbItem();
			tbItem.setTitle(tbGoods.getGoodsName());
			tbItem.setPrice(tbGoods.getPrice());
			tbItem.setNum(999);//默认一个
			tbItem.setStatus("1");//正常启用
			tbItem.setIsDefault("1");//默认的

			tbItem.setSpec("{}");


			//设置图片从goodsDesc中获取
			//[{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/03/wKgZhVq7N-qAEDgSAAJfMemqtP8461.jpg"}]
			String itemImages = goodsDesc.getItemImages();//

			List<Map> maps = JSON.parseArray(itemImages, Map.class);

			String url = maps.get(0).get("url").toString();//图片的地址
			tbItem.setImage(url);

			//设置分类
			TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
			tbItem.setCategoryid(tbItemCat.getId());
			tbItem.setCategory(tbItemCat.getName());

			//时间
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(new Date());

			//设置SPU的ID
			tbItem.setGoodsId(tbGoods.getId());

			//设置商家
			TbSeller tbSeller = sellerMapper.selectByPrimaryKey(tbGoods.getSellerId());
			tbItem.setSellerId(tbSeller.getSellerId());
			tbItem.setSeller(tbSeller.getNickName());//店铺名

			//设置品牌明后
			TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(tbGoods.getBrandId());
			tbItem.setBrand(tbBrand.getName());
			itemMapper.insert(tbItem);
		}
	}

	@Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbGoods> all = goodsMapper.selectAll();
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods goods) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        //逻辑删除的不查询,
		criteria.andEqualTo("isDelete",false);

        if(goods!=null){			
						if(StringUtils.isNotBlank(goods.getSellerId())){
				//criteria.andLike("sellerId","%"+goods.getSellerId()+"%");
				//将模糊匹配改为精确匹配
				criteria.andEqualTo("sellerId",goods.getSellerId());
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(goods.getGoodsName())){
				criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
				//criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(StringUtils.isNotBlank(goods.getAuditStatus())){
				criteria.andLike("auditStatus","%"+goods.getAuditStatus()+"%");
				//criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsMarketable())){
				criteria.andLike("isMarketable","%"+goods.getIsMarketable()+"%");
				//criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(StringUtils.isNotBlank(goods.getCaption())){
				criteria.andLike("caption","%"+goods.getCaption()+"%");
				//criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(StringUtils.isNotBlank(goods.getSmallPic())){
				criteria.andLike("smallPic","%"+goods.getSmallPic()+"%");
				//criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(StringUtils.isNotBlank(goods.getIsEnableSpec())){
				criteria.andLike("isEnableSpec","%"+goods.getIsEnableSpec()+"%");
				//criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
	
		}
        List<TbGoods> all = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> info = new PageInfo<TbGoods>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbGoods> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
