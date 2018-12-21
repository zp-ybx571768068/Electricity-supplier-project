package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     *  商品上下架功能
     * @param ids
     * @param marketable
     * @return
     */
    @PostMapping("/isPutAway")
    public Result isPutAway(Long[] ids ,String marketable){
       return goodsService.isPutAway(ids,marketable);
    }

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //设置商家
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(name);
            goods.getGoods().setAuditStatus("0");

            goodsService.addGoods(goods);
            return Result.success("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("增加失败");
    }

    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //校验商家
            TbGoods tbGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!sellerId.equals(tbGoods.getSellerId()) || !sellerId.equals(goods.getGoods().getSellerId())){
                return Result.error("操作违法");
            }
            goodsService.updateGoods(goods);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }

    /**
     * 提交审核
     * @param ids
     * @param status
     * @return
     */
    @GetMapping("submitAudit")
    public Result submitAudit(Long[] ids,String status){
        try {
            goodsService.submitAudit(ids,status);

            return Result.success("更新成功");
        }catch (Exception e){
         e.printStackTrace();
        }
        return Result.error("更新失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteById(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        return goodsService.search(page, rows, goods);
    }

}
