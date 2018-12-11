package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zp
 */
@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     *   http://manage.pinyougou.com/admin/brand.html
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer rows){
        return brandService.findPage(page,rows);
    }

    /**
     *  分页查询品牌列表数据
     *  http://localhost:9100/brand/testPage.do?page=1&rows
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(Integer page, Integer rows){
        return (List<TbBrand>)brandService.findPage(page,rows).getRows();
    }

    @RequestMapping ("/findAll")
    public List<TbBrand> queryAll(){
        //return brandService.queryAll();
        return brandService.findAll();
    }
}
