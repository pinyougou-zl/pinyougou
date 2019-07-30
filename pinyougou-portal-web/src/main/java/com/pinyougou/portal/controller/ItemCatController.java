package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.ItemQuery;
import com.entity.Result;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbItemCat> findAll() {
        return itemCatService.findAll();
    }

    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids, @PathVariable String status) {
        try {
            itemCatService.updateStatus(ids, status);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    @RequestMapping("/findPage")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return itemCatService.findPage(pageNo, pageSize);
    }

    @RequestMapping("/findParentName/{parentId}")
    public Result findParentName(@PathVariable Long parentId) {
        try {
            TbItemCat itemCat = itemCatService.findOne(parentId);
            return new Result(true, itemCat.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "异常");
        }
    }

    /**
     * 增加
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.add(itemCat);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbItemCat findOne(@PathVariable(value = "id") Long id) {
        return itemCatService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            itemCatService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    @RequestMapping("/search")
    public PageInfo<TbItemCat> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                        @RequestBody TbItemCat itemCat) {
        return itemCatService.findPage(pageNo, pageSize, itemCat);
    }

    /**
     * 查询父类名称
     *
     * @param parentId
     * @return
     */
    @RequestMapping("/findByParentId/{parentId}")
    public List<TbItemCat> findByParentId(@PathVariable(value = "parentId") Long parentId) {
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 前台页面分类查询
     *
     * @param parentId
     * @return
     */
    @RequestMapping("/goodsItem/{parentId}")
    public List<ItemQuery> goodsItem(@PathVariable Long parentId) {

        List<ItemQuery> queryList = itemCatService.findGoodItem(parentId);
        return queryList;

    }
}