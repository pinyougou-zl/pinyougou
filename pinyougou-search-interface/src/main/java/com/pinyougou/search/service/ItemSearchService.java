package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索条件搜索内容展示数据返回
     */
    Map<String,Object> search(Map<String,Object> searchMap);

    /**
     * 更新是数据到索引库中
     */
    public void updateIndex(List<TbItem> items);

    /**
     * 删除索引
     */
    public void deleteByIds(Long[] ids);
}
