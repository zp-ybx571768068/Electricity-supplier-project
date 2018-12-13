package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zp
 */
@RequestMapping("/brand")
@RestController
public class BrandController {

    @Reference
    private BrandService brandService;


    @PostMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer rows){
        PageResult search = brandService.search(brand, page, rows);
        long total = search.getTotal();
        if (total<(page-1)*rows){
            return brandService.search(brand, 1, rows);
        }

        return   brandService.search(brand, page, rows);
    }


    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteById(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Result.error("添加失败");
    }
    /**
     *   http://manage.pinyougou.com/admin/brand.html
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer rows){

        PageResult result = brandService.findPage(page, rows);
        return result;
    }

    /**
     *  分页查询品牌列表数据
     *  http://localhost:9100/brand/testPage.do?page=1&rows=5
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
