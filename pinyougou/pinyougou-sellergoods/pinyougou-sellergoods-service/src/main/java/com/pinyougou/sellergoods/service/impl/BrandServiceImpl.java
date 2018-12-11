package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;

/**
 * @author zp
 */
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    /**
     * 查询全部品牌列表
     *
     * @return List<TbBrand>
     */
    @Override
    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    /**
     *  分页查询品牌列表数据
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {

        PageHelper.startPage(page,rows);

        return brandMapper.selectAll();

    }
}
