package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.List;

import com.pinyougou.pojo.TbTypeTemplate;
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

}
