package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author zp
 */
public interface ItemSearchService {

    /**
     * 根据搜索关键字和其它条件到solr中查询数据
     * @param searchMap 搜索条件
     * @return 查询结果
     */
    Map<String,Object> search(Map<String, Object> searchMap);

    /**
     *  批量导入商品到solr索引库中
     * @param itemList 商品列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     *  根据goodsid集合删除对应其在solr中的数据
     * @param goodsIdList
     */
    void deleteItemByGoodsIdList(List<Long> goodsIdList);
}
