package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SpecificationMapper;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.sellergoods.service.SpecificationService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SpecificationService.class)
public class SpecificationServiceImpl extends BaseServiceImpl<TbSpecification> implements SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;
    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbSpecification specification) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSpecification.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isEmpty(specification.getSpecName())){
            criteria.andLike("specName", "%" + specification.getSpecName() + "%");
        }

        List<TbSpecification> list = specificationMapper.selectByExample(example);
        PageInfo<TbSpecification> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 修改add方法
     *      {"specificationOptionList":[{"optionName":"汝窑白","orders":"1"},{"optionName":"磨砂黑","orders":"2"}],
     *      "specification":{"specName":"颜色"}}
     * @param specification
     * @return Result
     */
    @Override
    public void add(Specification specification) {

        //添加specification
        specificationMapper.insertSelective(specification.getSpecification());

        //
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        if (specificationOptionList != null && specificationOptionList.size() > 0){

            for (TbSpecificationOption SpecificationOption : specificationOptionList){
                SpecificationOption.setSpecId(specification.getSpecification().getId());
            //保存选项规格
                specificationOptionMapper.insert(SpecificationOption);
            }
        }


    }

    /**
     *  回显规格和规格列表功能
     * @param id
     * @return Specification
     */
    @Override
    public Specification findOne(Long id) {

        //创建Specification对象用户封装查询数据
        Specification specification = new Specification();
        //根据id查找规格
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specification.setSpecification(tbSpecification);

        //根据规格id查找规格选项列表
        List<TbSpecificationOption> tbSpecificationOption = specificationOptionMapper.findOne(id);
        specification.setSpecificationOptionList(tbSpecificationOption);

        return specification;
    }

    /**
     *  修改规格和规格选项列表数据
     * @param specification
     */
    @Override
    public void update(Specification specification) {

        //修改规格
        specificationMapper.updateByPrimaryKeySelective(specification.getSpecification());

        //根据规格id删除规格选项再进行保存新的规格选项
        specificationOptionMapper.deleteBySpecifitionId(specification.getSpecification().getId());

        //保存新的规格选项
        if (specification.getSpecificationOptionList() != null && specification.getSpecificationOptionList().size() > 0){
            for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()){

                tbSpecificationOption.setSpecId(specification.getSpecification().getId());
                specificationOptionMapper.insertSelective(tbSpecificationOption);
            }
        }

    }

    /**
     *  根据规格id数组删除规格选项
     * @param ids
     */
    @Override
    public void deleteSpecificationByIds(Long[] ids) {

        //批量删除规格
        deleteById(ids);

        //根据规格id数组批量删除规格选项
        Example example = new Example(TbSpecificationOption.class);

        example.createCriteria().andIn("specId",Arrays.asList(ids));

        specificationOptionMapper.deleteByExample(example);

    }

    /**
     * 加载select2的规格数据列表；格式：[{id:'1',text:'内存'},{id:'2',text:'尺寸'}]
     * @return 规格数据列表
     */
    @Override
    public List<Map<String, Object>> selectOptionList() {
        return specificationMapper.selectOptionList();
    }
}
