package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    Result updateStatus(Long[] ids, String status);

    void deleteGoodsByIds(Long[] ids);

    Result isPutAway(Long[] ids, String marketable);

    void submitAudit(Long[] ids, String status);

    /**
     *  根据SPU id集合和状态查询这些商品对应的sku列表
     * @param ids
     * @param status
     * @return
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    /**
     *  根据商品id查询商品基本、描述、sku列表已启用信息
     * @param goodsId 商品id
     * @param status 已启用
     * @return 商品
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String status);
}