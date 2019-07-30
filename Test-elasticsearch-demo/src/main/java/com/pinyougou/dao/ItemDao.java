package com.pinyougou.dao;

import com.pinyougou.model.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 继承该接口获得该接口中的方法，封装了es的操作
 */
public interface ItemDao extends ElasticsearchRepository<TbItem,Long> {

}
