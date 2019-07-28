package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.entity.TypeTemplate;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;  

import com.pinyougou.sellergoods.service.TypeTemplateService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl extends CoreServiceImpl<TbTypeTemplate>  implements TypeTemplateService {

	//注入redis
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSpecificationOptionMapper optionMapper;
	private TbTypeTemplateMapper typeTemplateMapper;

	@Autowired
	public TypeTemplateServiceImpl(TbTypeTemplateMapper typeTemplateMapper) {
		super(typeTemplateMapper, TbTypeTemplate.class);
		this.typeTemplateMapper=typeTemplateMapper;
	}

	/**
	 * 根据id查询商品的规格的信息
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> findSpecList(Long id) {
		//1、根据主键查询 ，获取模板的对象
		TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
		//2、获取模板对象中的规格列表[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		String specIds = tbTypeTemplate.getSpecIds();
		//3、将字符串转成JSON对象数组
		List<Map> maps = JSON.parseArray(specIds, Map.class);
		for (Map map : maps) {
			//[{"id":27,"text":"网络"}
			//4、循环遍历Json数组，根据规格的id获取规格的所有选项列表
			Integer id1 = (Integer) map.get("id");
			//select * from option where spec_id=id
			TbSpecificationOption option = new TbSpecificationOption();
			option.setSpecId(Long.valueOf(id1));
			//进行查询
			List<TbSpecificationOption> optionList = optionMapper.select(option);
			//5、拼接成：[{"id":27,"text":"网络",optionsList:[{optionName:'移动3G'},{optionName:'移动4G'}]},{"id":32,"text":"机身内存"}]
			map.put("optionList",optionList);
		}
		return maps;
	}

	@Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        //查询为审核的模板
		Example example = new Example(TbTypeTemplate.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status","0");
		List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
		//List<TbTypeTemplate> all = typeTemplateMapper.selectAll();
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


	/**
	 * 修改运营商后台模板管理页面查询findPage方法：增加查询条件：查询已审核的模板
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @param typeTemplate
	 * @return
	 */
	 @Override
    public PageInfo<TbTypeTemplate> findPage(Integer pageNo, Integer pageSize, TbTypeTemplate typeTemplate) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbTypeTemplate.class);
        Example.Criteria criteria = example.createCriteria();

        if(typeTemplate!=null){			
						if(StringUtils.isNotBlank(typeTemplate.getName())){
				criteria.andLike("name","%"+typeTemplate.getName()+"%");
				//criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getSpecIds())){
				criteria.andLike("specIds","%"+typeTemplate.getSpecIds()+"%");
				//criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getBrandIds())){
				criteria.andLike("brandIds","%"+typeTemplate.getBrandIds()+"%");
				//criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(StringUtils.isNotBlank(typeTemplate.getCustomAttributeItems())){
				criteria.andLike("customAttributeItems","%"+typeTemplate.getCustomAttributeItems()+"%");
				//criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		criteria.andEqualTo("status","1");
        List<TbTypeTemplate> all = typeTemplateMapper.selectByExample(example);
        PageInfo<TbTypeTemplate> info = new PageInfo<TbTypeTemplate>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbTypeTemplate> pageInfo = JSON.parseObject(s, PageInfo.class);

		 /**
		  * 存入redis缓存
		  */
		 List<TbTypeTemplate> all1 = this.findAll();
		 for (TbTypeTemplate tbTypeTemplate : all1) {
			//存储品牌列表
		 	List<Map> mapList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
			 redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(),mapList);
			 //存储规格列表
			 List<Map> specList = findSpecList(tbTypeTemplate.getId());//根据模板id查询规格列表信息
			 redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(),specList);
		 }

		 return pageInfo;
    }

	@Override
	public void updateStatus(Long[] ids, String status) {
		TbTypeTemplate typeTemplate = new TbTypeTemplate();
		typeTemplate.setStatus(status);
		Example example = new Example(TbTypeTemplate.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", Arrays.asList(ids));
		typeTemplateMapper.updateByExampleSelective(typeTemplate,example);
	}

}
