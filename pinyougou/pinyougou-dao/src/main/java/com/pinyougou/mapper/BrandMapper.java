package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;


import java.util.List;
import java.util.Map;

/**
 * @author zp
 */
public interface BrandMapper extends Mapper<TbBrand> {

    /**
     *  查询全部品牌列表
     * @return List<TbBrand>
     */
    List<TbBrand> queryAll();

    List<Map<String,Object>> selectOptionList();
}
