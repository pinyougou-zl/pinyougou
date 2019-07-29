package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.entity.ItemQuery;
import com.pinyougou.common.util.SysConstants;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;  

import com.pinyougou.sellergoods.service.ItemCatService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl extends CoreServiceImpl<TbItemCat>  implements ItemCatService {

    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;
	
	private TbItemCatMapper itemCatMapper;

	@Autowired
	public ItemCatServiceImpl(TbItemCatMapper itemCatMapper) {
		super(itemCatMapper, TbItemCat.class);
		this.itemCatMapper=itemCatMapper;
	}

    /**
     * 新增方法，跟据parentId查询TbItemCat信息
     * @param parentId
     * @return
     */
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCat cat = new TbItemCat();
        cat.setParentId(parentId);
        //根据条件查询
        List<TbItemCat> catList = itemCatMapper.select(cat);

        //每次执行查询的时候，一次性读取缓存进行存储（因为每次增删改都要执行该方法）
        //查出所有的数据
        List<TbItemCat> all = findAll();
        for (TbItemCat tbItemCat : all) {
            redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(),
                    tbItemCat.getTypeId());
        }
        return catList;
    }

    @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","0");
        List<TbItemCat> all = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbItemCat> findPage(Integer pageNo, Integer pageSize, TbItemCat itemCat) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();

        if(itemCat!=null){			
						if(StringUtils.isNotBlank(itemCat.getName())){
				criteria.andLike("name","%"+itemCat.getName()+"%");
				//criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
		criteria.andEqualTo("status","1");
        List<TbItemCat> all = itemCatMapper.selectByExample(example);

        PageInfo<TbItemCat> info = new PageInfo<TbItemCat>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbItemCat> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbItemCat itemCat = new TbItemCat();
        itemCat.setStatus(status);
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        itemCatMapper.updateByExampleSelective(itemCat,example);
        //itemCatMapper.updateByExample(itemCat,example);
    }



    /**
     * 前台页面分类展示
     * @param parentId
     */
    @Override
    public List<ItemQuery> findGoodItem(Long parentId) {
        //通过顶级id在redis中查询是否存在数据
        List<ItemQuery> itemCats = (List<ItemQuery>) redisTemplate.boundValueOps(SysConstants.YM).get();
        //如果没有就到数据库中查询并且存入redis
        if (itemCats==null) {
            //通过parentId到数据库中查询

            List<TbItemCat> itemCats1 = itemCatMapper.selectByParentId(parentId);
           itemCats = new ArrayList<>();
            //顶级商品分类
            for (TbItemCat tbItemCat : itemCats1) {
                ItemQuery itemCat01 = new ItemQuery();
                itemCat01.setId(tbItemCat.getId());
                itemCat01.setName(tbItemCat.getName());
                itemCat01.setParentId(tbItemCat.getParentId());
                //创建一个放二级分类的集合
                List<ItemQuery> itemCatList02 = new ArrayList<>();
                //通过顶级商品分类的id查询二级商品分类 并且是要已经审核的
                Example example2 = new Example(TbItemCat.class);
                example2.createCriteria().andEqualTo("parentId",tbItemCat.getId()).andEqualTo("status","1");
                List<TbItemCat> itemCats2 = itemCatMapper.selectByExample(example2);
                //通过二级分类的id查询三级商品分类 并且是要已经审核的
                for (TbItemCat cat : itemCats2) {

                    ItemQuery itemCat02 = new ItemQuery();
                    itemCat02.setId(cat.getId());
                    itemCat02.setName(cat.getName());
                    itemCat02.setParentId(cat.getParentId());

                    Example example3 = new Example(TbItemCat.class);
                    example3.createCriteria().andEqualTo("parentId",cat.getId()).andEqualTo("status","1");
                    List<TbItemCat> itemCats3 = itemCatMapper.selectByExample(example3);
                    //设置三级分类到二级分类的list中
                    itemCat02.setItemCatList(itemCats3);
                    itemCatList02.add(itemCat02);
                }
                //设置二级分类到顶级分类的List中
                itemCat01.setItemCatList(itemCats2);
                itemCats.add(itemCat01);
            }
            //存入redis
            redisTemplate.boundValueOps(SysConstants.YM).set(itemCats);
            return itemCats;
        }else {
            //如果有就返回从redis中查询到的数据
            return itemCats;
        }
    }

}
