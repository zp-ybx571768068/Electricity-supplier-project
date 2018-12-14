package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

public interface SpecificationOptionService extends BaseService<TbSpecificationOption> {

    PageResult search(Integer page, Integer rows, TbSpecificationOption specificationOption);


}