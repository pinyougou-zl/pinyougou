package com.pinyougou.sellergoods.service;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService extends CoreService<TbBrand> {
    /*//查询所有
    List<TbBrand> findAll();*/

    //分页查询
    public PageInfo<TbBrand> findPage(Integer pageNo,Integer pageSize);

   /* //增加品牌
    void add(TbBrand brand);*/

    /*//修改品牌
    void update(TbBrand brand);
    //修改之前需要回显,也就是根据id进行查询
    TbBrand findOne(Long id);*/

   /* //删除品牌
    void delete(Long[] ids);*/

    //前端的条件查询，查询出来的需要进行分页
    //进行一个方法的重载
    PageInfo<TbBrand> findPage(Integer pageNo,Integer pageSize,TbBrand brand);


    PageInfo<TbBrand> oneFindPage(Integer pageNo, Integer pageSize, TbBrand brand);

}
