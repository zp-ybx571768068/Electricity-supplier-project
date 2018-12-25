package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.ctc.wstx.util.StringUtil;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zp
 */
@Service(interfaceClass = ItemSearchService.class )
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 根据搜索关键字和其它条件到solr中查询数据
     *
     * @param searchMap 搜索条件
     * @return 查询结果
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> requestMap = new HashMap<>();


        //处理搜索关键字中的空格问题
        if (!StringUtils.isEmpty(searchMap.get("keywords").toString())) {
            searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));
        }


        //创建高亮搜索对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照分类过滤
        if (!StringUtils.isEmpty(searchMap.get("category").toString())) {
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
        //按照品牌过滤
        if (!StringUtils.isEmpty(searchMap.get("brand").toString())) {
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(simpleFilterQuery);
        }
        //按照规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Criteria specCriteria = new Criteria("item_spec_"+ entry.getKey()).is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(simpleFilterQuery);
            }
        }

        //按照价格区间过滤
        if (!StringUtils.isEmpty(searchMap.get("price").toString())){
            //获取起始、结束价格
            String[] prices = searchMap.get("price").toString().split("-");

            //价格大于或等于起始价格
            Criteria startPriceCriteria = new Criteria("item_price").greaterThan(prices[0]);
            SimpleFilterQuery startPriceFilterQuery = new SimpleFilterQuery(startPriceCriteria);
            query.addFilterQuery(startPriceFilterQuery);

            //价格小于等于结束价格
            if (!"*".equals(prices[1])){
                Criteria endPriceCriteria = new Criteria("item_price").lessThan(prices[1]);
                SimpleFilterQuery endPriceFilterQuery = new SimpleFilterQuery(endPriceCriteria);
                query.addFilterQuery(endPriceFilterQuery);
            }
        }
        //设置分页信息
        Integer pageNo = 1;
        if (!StringUtils.isEmpty(searchMap.get("pageNo").toString())){
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        Integer pageSize = 20;
        if (!StringUtils.isEmpty(searchMap.get("pageSize").toString())){
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);


            //设置高亮
            HighlightOptions highlightOptions = new HighlightOptions();
            //高亮域
            highlightOptions.addField("item_title");
            //高亮起始标签
            highlightOptions.setSimplePrefix("<em style='color:red'>");
            //高亮标签结束
            highlightOptions.setSimplePostfix("</em>");
            query.setHighlightOptions(highlightOptions);

            //查询
            HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

            //处理高亮标题
            List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();
            if (highlighted != null && highlighted.size() > 0) {

                for (HighlightEntry<TbItem> entry : highlighted) {
                    //获取高亮的列表
                    List<HighlightEntry.Highlight> highlights = entry.getHighlights();

                    if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null) {
                        //第一个get（0）为获取第一个域，第二个get（0）为获取改域的第一个高亮字符串
                        String title = highlights.get(0).getSnipplets().get(0).toString();
                        //设置高亮标题
                        entry.getEntity().setTitle(title);
                    }
                }
            }
            //设置返回的商品列表
            requestMap.put("rows", itemHighlightPage.getContent());
            requestMap.put("totalPages",itemHighlightPage.getTotalPages());
            requestMap.put("total",itemHighlightPage.getTotalElements());

            return requestMap;
        }
    }

