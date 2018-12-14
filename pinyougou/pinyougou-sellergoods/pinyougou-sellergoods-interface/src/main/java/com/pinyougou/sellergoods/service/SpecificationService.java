package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    /**
     * 修改add方法
     *      {"specificationOptionList":[{"optionName":"汝窑白","orders":"1"},{"optionName":"磨砂黑","orders":"2"}],
     *      "specification":{"specName":"颜色"}}
     * @param specification
     * @return Result
     */
    void add(Specification specification);

    /**
     *  回显规格和规格列表功能
     * @param id
     * @return
     */
    Specification findOne(Long id);

    /**
     *  修改规格和规格选项列表数据
     * @param specification
     */
    void update(Specification specification);

    /**
     *  根据规格id数组删除规格选项
     * @param ids
     */
    void deleteSpecificationByIds(Long[] ids);

    /**
     * 加载select2的规格数据列表；格式：[{id:'1',text:'内存'},{id:'2',text:'尺寸'}]
     * @return 规格数据列表
     */
    List<Map<String, Object>> selectOptionList();
}