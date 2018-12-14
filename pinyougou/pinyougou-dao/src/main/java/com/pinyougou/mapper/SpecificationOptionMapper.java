package com.pinyougou.mapper;

import com.pinyougou.pojo.TbSpecificationOption;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecificationOptionMapper extends Mapper<TbSpecificationOption> {

    //根据规格id查找规格选项列表
    List<TbSpecificationOption> findOne(Long specId);

    /**
     *  根据规格id删除规格选项
     * @param id
     */
    void deleteBySpecifitionId(Long id);
}
