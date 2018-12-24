package com.pinyougou.search.service;

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
}
