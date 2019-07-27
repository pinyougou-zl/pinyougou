package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

/**
 * 包名:com.itheima.es.dao
 * 作者:yang
 * 日期:19-7-10 上午10:31
 */
public interface ItemDao extends ElasticsearchCrudRepository<TbItem,Long> {
}
