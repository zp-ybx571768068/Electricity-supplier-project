package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    List<Map<String, Object>> selectOptionList();

    /**
     *  根据分类模板id查询对应的规格和规格选项
     * @param id
     * @return
     */
    List<Map> findSpecList(Long id);
}