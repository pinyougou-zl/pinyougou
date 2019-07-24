package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.Result;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(value = "pageNo",defaultValue = "1",required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize",defaultValue = "10",required = true) Integer pageSize) {
        return brandService.findPage(pageNo,pageSize);
    }

    /**
     * 增加品牌
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        //System.out.println(brand.getName());
        try {
            brandService.add(brand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改品牌
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        System.out.println(brand);
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }

    /**
     * 数据进行回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbBrand findOne(@PathVariable(value = "id") Long id) {
        System.out.println(id);
        TbBrand one = brandService.findOne(id);
        return one;
    }

    /**
     * 删除品牌
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] id) {
        System.out.println(Arrays.toString(id));
        try {
            brandService.delete(id);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageInfo<TbBrand> search(@RequestParam(value = "pageNo",defaultValue = "1",required = true) Integer pageNo,
                                    @RequestParam(value = "pageSize",defaultValue = "5",required = true) Integer pageSize,
                                    @RequestBody  TbBrand brand) {
        return brandService.findPage(pageNo,pageSize,brand);
    }
}
