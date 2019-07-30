package com.pinyougou.sellergoods.service.impl;

import java.util.Arrays;
import java.util.List;

import com.entity.Specification;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;  

import com.pinyougou.sellergoods.service.SpecificationService;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl extends CoreServiceImpl<TbSpecification>  implements SpecificationService {

	private TbSpecificationMapper specificationMapper;
	//由于要关联两个表，所以还要注入一个对象
    @Autowired
    private TbSpecificationOptionMapper optionMapper;

	@Autowired
	public SpecificationServiceImpl(TbSpecificationMapper specificationMapper) {
		super(specificationMapper, TbSpecification.class);
		this.specificationMapper=specificationMapper;
	}

    /**
     * 规格增加的方法  一对多的关系
     * @param specification
     */
    @Override
    public void add(Specification specification) {
        //在传入的对象中取出specification对象
        TbSpecification specification1 = specification.getSpecification();
        //进行增加
        specificationMapper.insert(specification1);
        //对规格选项进行查询
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption option : optionList) {  //option就是一个规格对象，spce-id就是规格的主键
           option.setSpecId(specification1.getId());
            optionMapper.insert(option);
        }
    }

    /**
     * 根据id进行回显操作
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        Specification specification = new Specification();
        //根据id主键进行查询
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(tbSpecification.getId());
        List<TbSpecificationOption> select = optionMapper.select(option);
        specification.setSpecification(tbSpecification);
        specification.setOptionList(select);
        return specification;
    }

    /**
     *修改规格
     * @param specification
     */
    @Override
    public void update(Specification specification) {
        TbSpecification specification1 = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(specification1);
        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(specification1.getId());
        //先进行删除
        int delete = optionMapper.delete(option);
        //在进行添加，因为我们所修改的数据都在对象里，取出来再存就可以了
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption tbSpecificationOption : optionList) {
            tbSpecificationOption.setSpecId(specification1.getId());
            optionMapper.insert(tbSpecificationOption);
        }
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {
        //删除规格,以为参数里的id值是specification的id
        Example example = new Example(TbSpecification.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        specificationMapper.deleteByExample(example);
        //删除规格关联的规格选项
        Example example1 = new Example(TbSpecificationOption.class);
        example1.createCriteria().andIn("specId",Arrays.asList(ids));
        optionMapper.deleteByExample(example1);
    }

    /**
     * 分页
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSpecification> all = specificationMapper.selectAll();
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

    /**
     * 条件模糊查询进行分页
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @param specification
     * @return
     */
	 @Override
    public PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){			
						if(StringUtils.isNotBlank(specification.getSpecName())){
				criteria.andLike("specName","%"+specification.getSpecName()+"%");
				//criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }


    /**
     * 防冲突=====================================================
     * 查
     */
    @Override
    public PageInfo<TbSpecification> oneFindPage(Integer pageNo, Integer pageSize, TbSpecification specification) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();

        if(specification!=null){
            if(StringUtils.isNotBlank(specification.getSpecName())){
                criteria.andLike("specName","%"+specification.getSpecName()+"%");
                //criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }
            if(StringUtils.isNotBlank(specification.getSellerId())){
                criteria.andEqualTo("sellerId",specification.getSellerId());
                //criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
            }


        }
        List<TbSpecification> all = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> info = new PageInfo<TbSpecification>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSpecification> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
    //保存修改数据2
    @Override
    public void updateApply(Specification specification) {
        specificationMapper.updateByPrimaryKey(specification.getSpecification());
        TbSpecificationOption option1 = new TbSpecificationOption();
        option1.setSpecId(specification.getSpecification().getId());
        optionMapper.delete(option1);
        List<TbSpecificationOption> optionList = specification.getOptionList();
        for (TbSpecificationOption option : optionList) {
            option.setSpecId(option1.getSpecId());
            System.out.println(option);
            optionMapper.insert(option);

        }
    }

    //获取数据回显
    @Override
    public Specification oneFindOne(Long id) {
        Specification specification = new Specification();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

        TbSpecificationOption option = new TbSpecificationOption();
        option.setSpecId(id);
        List<TbSpecificationOption> options = optionMapper.select(option);
        specification.setOptionList(options);
        specification.setSpecification(tbSpecification);
        return specification;

    }


	
}
