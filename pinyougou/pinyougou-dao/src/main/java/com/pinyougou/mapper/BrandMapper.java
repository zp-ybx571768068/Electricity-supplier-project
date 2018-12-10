package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * @author zp
 */
public interface BrandMapper {

    /**
     *  查询全部品牌列表
     * @return List<TbBrand>
     */
    List<TbBrand> queryAll();
}
