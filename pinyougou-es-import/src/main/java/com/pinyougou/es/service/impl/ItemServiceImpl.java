package com.pinyougou.es.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemDao;
import com.pinyougou.es.service.ItemService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public void ImportDataToes() {
        //1、从数据库查询出符合条件的查询
        TbItem item = new TbItem();
        item.setStatus("1");  //审核过的
        List<TbItem> itemList = itemMapper.select(item);
        for (TbItem tbItem : itemList) {
            String spec = tbItem.getSpec();
            if( StringUtils.isNotBlank(spec) ) {
                Map map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }
        }
        itemDao.saveAll(itemList);
    }
}
