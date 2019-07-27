package com.lj.test;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/spring-mybatis.xml")
public class TestMybatis {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Test
    public void fun() {
       /* List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        for (TbBrand tbBrand : tbBrands) {
            System.out.println(tbBrand);
        }*/
        PageHelper.startPage(1,5);
        List<TbBrand> tbBrands = tbBrandMapper.selectAll();
        PageInfo<TbBrand> info = new PageInfo<>(tbBrands);
        System.out.println(info);
        //进行序列化，再进行反序列化
        String s = JSON.toJSONString(info);
        PageInfo pageInfo = JSON.parseObject(s,PageInfo.class);
        System.out.println(info);
    }
}
