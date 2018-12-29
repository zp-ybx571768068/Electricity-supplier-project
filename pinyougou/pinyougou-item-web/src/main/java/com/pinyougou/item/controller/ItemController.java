package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zp
 */
@Controller
public class ItemController {

    @Reference
    private GoodsService   goodsService;
    @Reference
    private ItemCatService itemCatService;

    /**
     *  跳转到商品详情页面显示商品
     * @param goodsId
     * @return
     */
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId){
        ModelAndView mv = new ModelAndView("item");

        //根据商品id查询商品基本、描述、sku列表已启用信息
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");
        //商品基本信息
        mv.addObject("goods",goods.getGoods());
        //商品描述信息
        mv.addObject("goodsDesc",goods.getGoodsDesc());
        //sku商品列表
        mv.addObject("itemList", goods.getItemList());
        //一级分类
        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1",itemCat1.getName());
        //二级分类
        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2",itemCat2.getName());
        //三级分类
        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());

        mv.addObject("itemCat3",itemCat3.getName());


        return mv;
    }
}
