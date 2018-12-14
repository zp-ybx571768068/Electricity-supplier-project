package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;

import java.util.List;
import java.util.Map;

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

    PageResult search(TbBrand brand, Integer page, Integer rows);

    List<Map<String, Object>> selectOptionList();
}
