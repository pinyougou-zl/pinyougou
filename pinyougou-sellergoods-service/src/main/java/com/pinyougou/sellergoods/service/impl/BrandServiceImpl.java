package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import tk.mybatis.mapper.entity.Example;

import java.util.List;
@Service
public class BrandServiceImpl extends CoreServiceImpl<TbBrand> implements BrandService {

    private TbBrandMapper tbBrandMapper;

    @Autowired
    public BrandServiceImpl(TbBrandMapper tbBrandMapper) {
        super(tbBrandMapper,TbBrand.class);
        this.tbBrandMapper = tbBrandMapper;
    }


    /**
     * 查询所有
     * @return
     */
    /*@Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectAll();
    }*/

    /**
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize) {
        //System.out.println(pageNo);
        //System.out.println(pageSize);
        PageHelper.startPage(pageNo,pageSize);
        List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        PageInfo<TbBrand> info = new PageInfo<>(tbBrands);
        //System.out.println(info);
        //进行序列化，再进行反序列化
        String s = JSON.toJSONString(info);
        PageInfo pageInfo = JSON.parseObject(s,PageInfo.class);
        return pageInfo;
    }

    /**
     * 增加品牌
     * @param brand
     */
   /* @Override
    public void add(TbBrand brand) {
        tbBrandMapper.insert(brand);
    }*/

    /**
     * 修改品牌
     * @param brand
     */
    /*@Override
    public void update(TbBrand brand) {
        tbBrandMapper.updateByPrimaryKey(brand);
    }
*/
    /**
     * 回显操作
     * @param id
     * @return
     */
    /*@Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }*/

    /*@Override
    public void delete(Long[] ids) {
        //需要进行条件查询
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        tbBrandMapper.deleteByExample(example);
    }*/

    @Override
    public PageInfo<TbBrand> findPage(Integer pageNo, Integer pageSize, TbBrand brand) {
        PageHelper.startPage(pageNo,pageSize);
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();

        //进行判断
        if(brand != null) {
            if(StringUtils.isNotBlank(brand.getName())) {  //非空
                criteria.andLike("name","%"+brand.getName()+"%");
            }

            if(StringUtils.isNotBlank(brand.getFirstChar())) {
                criteria.andLike("firstChar","%"+brand.getFirstChar()+"%");
            }
        }
        List<TbBrand> tbBrands = tbBrandMapper.selectByExample(example);
        PageInfo<TbBrand> info = new PageInfo<>(tbBrands);
        String s = JSON.toJSONString(info);
        PageInfo pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }



    /**
     * 本次项目防冲突
     * 查询:分页+条件
     * @param pageNo
     * @param pageSize
     * @param brand
     * @return
     */

    @Override
    public PageInfo<TbBrand> oneFindPage(Integer pageNo, Integer pageSize, TbBrand brand) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        if (brand != null) {
            if (StringUtils.isNotBlank(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
                //criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (StringUtils.isNotBlank(brand.getFirstChar())) {
                criteria.andLike("firstChar", "%" + brand.getFirstChar() + "%");
                //criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
            //匹配相同的sellerId
            if (StringUtils.isNotBlank(brand.getSellerId())) {
                criteria.andEqualTo("sellerId", brand.getSellerId());
                //criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }

        }
        List<TbBrand> all = brandMapper.selectByExample(example);
        PageInfo<TbBrand> info = new PageInfo<TbBrand>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbBrand> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;

    }
}
