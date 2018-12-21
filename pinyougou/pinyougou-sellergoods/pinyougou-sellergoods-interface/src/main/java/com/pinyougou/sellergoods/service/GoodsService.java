package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    Result updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    Result isPutAway(Long[] ids, String marketable);

    void submitAudit(Long[] ids, String status);
}