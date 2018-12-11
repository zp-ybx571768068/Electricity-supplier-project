package com.pinyougou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import java.io.Serializable;
import java.util.List;

public  class BaseServiceImpl<T> implements BaseService<T> {

    /**
     * spring 4.x 版本之后引入的泛型依赖注入
     */
    @Autowired
    private Mapper<T> mapper;

    /**
     * 根据主键查询
     *
     * @param id 主键字段
     * @return T
     */
    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 查询全部
     *
     * @return List<T>
     */
    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    /**
     * 根据条件查询
     *
     * @param t 条件
     * @return T
     */
    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    /**
     * 根据分页信息查询
     *
     * @param page 页号
     * @param rows 页大小
     * @return PageResult 分页实体对象
     */
    @Override
    public PageResult findPage(Integer page, Integer rows) {
        PageHelper.startPage(page, rows);
        List<T> list = mapper.selectAll();
        PageInfo<T> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 根据分页条件查询
     *
     * @param page 页号
     * @param rows 页大小
     * @param t    条件
     * @return PageResult 分页实体对象
     */
    @Override
    public PageResult findPage(Integer page, Integer rows, T t) {
        PageHelper.startPage(page, rows);
        List<T> list = mapper.select(t);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增
     *
     * @param t 实体对象
     */
    @Override
    public void add(T t) {
        mapper.insertSelective(t);
    }

    /**
     * 根据主键更新
     *
     * @param t 实体对象
     */
    @Override
    public void update(T t) {
        mapper.updateByPrimaryKeySelective(t);
    }

    /**
     * 根据主键批量删除
     *
     * @param ids 主键集合
     */
    @Override
    public void deleteById(Serializable[] ids) {

        if (ids != null && ids.length > 0) {

            for (Serializable id : ids) {
                mapper.deleteByPrimaryKey(id);
            }
        }


    }
}
