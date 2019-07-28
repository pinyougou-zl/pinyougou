package com.pinyougou.core.service;

import java.util.List;
import java.util.Map;

/**
 * 核心接口，抽取了接口中共同的curd方法
 * @param <T>
 */
public interface CoreService<T> {
    /**
     * 增加
     * @param t
     * @return
     */
    int insert(T t);

    /**
     * 增加
     * @param t
     */
    void add(T t);

    /**
     * 忽略空，添加数据
     * @param t
     * @return
     */
    int insertSelective(T t);

    /**
     * 根据实体对象作为条件来删除  条件为等号
     *
     * @param record
     * @return
     */
    int delete(T record);

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    void delete(Object[] ids);

    /**
     * 根据主键来删除
     *
     * @param key
     * @return
     */
    int deleteByPrimaryKey(Object key);

    /**
     * 根据条件来删除
     *
     * @param example
     * @return
     */
    int deleteByExample(Object example);


    /**
     * 根据条件 查询一条记录
     *
     * @param record
     * @return
     */
    T selectOne(T record);


    /**
     * 根据主键来查询
     *
     * @param id
     * @return
     */
    T findOne(Object id);


    /**
     * 根据条件查询列表记录  条件使用等号
     *
     * @param record 等号条件
     * @return
     */
    List<T> select(T record);


    /**
     * 查询所有
     *
     * @return
     */
    List<T> selectAll();

    /**
     * 查询所有
     *
     * @return
     */

    List<T> findAll();


    /**
     * 根据主键来查询
     *
     * @param key
     * @return
     */
    T selectByPrimaryKey(Object key);


    /**
     * 根据条件来查询
     *
     * @param example 这个为任意的条件
     * @return
     */
    List<T> selectByExample(Object example);


    /**
     * 修改 等同于updateByPrimaryKey
     *
     * @param record
     * @return
     */
    void update(T record);

    /**
     * 根据主键更新
     *
     * @param record 要更新的数据  一定要有主键的值  null 也会更新进去
     * @return
     */
    int updateByPrimaryKey(T record);


    /**
     * 根据主键来更新  更新的条件为 非空不更新
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(T record);

}
