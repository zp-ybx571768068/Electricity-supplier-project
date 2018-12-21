package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface ItemCatService extends BaseService<TbItemCat> {

    PageResult search(Integer page, Integer rows, TbItemCat itemCat);

    void addItemCat(TbItemCat itemCat,Long parentId);

    void deleteByItemCatIds(Long[] ids);




    /* void addGoods(TbItemCat itemCat);*/
}