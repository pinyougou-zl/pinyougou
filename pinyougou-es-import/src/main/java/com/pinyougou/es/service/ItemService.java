package com.pinyougou.es.service;

public interface ItemService {
    /**
     * 从数据库中获取数据 导入到ES的索引库
     */
    public void importDataToEs();

    /**
     * @param ids
     * @Description : 根据id从数据库中获取数据，导入到ES的索引库
     * @Return : void
     * @Author : yang
     */
    public void importDataToEs(Long[] ids);

    /**
     * @param ids
     * @Description : 根据id从ES索引库中删除数据
     * @Return : void
     * @Author : yang
     */
    public void deleteDataFromEsByID(Long[] ids);
}