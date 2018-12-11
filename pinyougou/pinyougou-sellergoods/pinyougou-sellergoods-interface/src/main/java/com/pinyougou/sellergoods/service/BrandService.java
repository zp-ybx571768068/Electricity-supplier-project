package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;

import java.util.List;

/**
 * @author zp
 */
public interface BrandService extends BaseService<TbBrand> {
    /**
     *  查询全部品牌列表
     * @return List<TbBrand>
     */
    List<TbBrand> queryAll();

    /**
     *  分页查询品牌列表数据
     * @param page
     * @param rows
     * @return
     */
    List<TbBrand> testPage(Integer page, Integer rows);
}
