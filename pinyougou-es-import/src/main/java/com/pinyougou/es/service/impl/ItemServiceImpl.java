package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 包名:com.pinyougou.es.service.impl
 * 作者:yang
 * 日期:19-7-10 下午3:46
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDao dao;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void importDataToEs() {
        //1.从数据库查询出符合条件的tbitem的数据

        TbItem record = new TbItem();
        record.setStatus("1");//审核过的
        List<TbItem> itemList = itemMapper.select(record);
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            if (StringUtils.isNotBlank(spec)) {
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }
        }
        //2.保存即可
        dao.saveAll(itemList);
    }

    public void importDataToEs(Long[] ids) {
        List<TbItem> itemList = new ArrayList<>();
        for (Long id : ids) {
            //1.从数据库查询出符合条件的tbitem的数据
            TbItem tbItem = itemMapper.selectByPrimaryKey(id);
            String spec = tbItem.getSpec();
            if (StringUtils.isNotBlank(spec)) {
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }
            itemList.add(tbItem);
        }
        //2.保存即可
        dao.saveAll(itemList);
    }

    public void deleteDataFromEsByID(Long[] ids) {
        if (ids != null && ids.length >= 1) {
            for (Long id : ids) {
                dao.deleteById(id);
            }
        }
    }

}
