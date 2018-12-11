package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {



    /**
     *  根据主键查询
     * @param id 主键字段
     * @return T
     */
    public T findOne(Serializable id);

    /**
     *  查询全部
     * @return List<T>
     */
    public List<T> findAll();

    /**
     *  根据条件查询
     * @param t 条件
     * @return T
     */
    public List<T> findByWhere(T t);


    /**
     *  根据分页信息查询
     * @param page 页号
     * @param rows 页大小
     * @return PageResult 分页实体对象
     */
    public PageResult findPage(Integer page, Integer rows);

    /**
     *  根据分页条件查询
     * @param page 页号
     * @param rows 页大小
     * @param t 条件
     * @return  PageResult 分页实体对象
     */
    public PageResult findPage (Integer page, Integer rows,T t);

    /**
     *  新增
     * @param t 实体对象
     */
    public void add(T t);

    /**
     * 根据主键更新
     * @param t  实体对象
     */
    public void update(T t);

    /**
     *  根据主键批量删除
     * @param ids 主键集合
     */
    public void deleteById(Serializable[] ids);
}
