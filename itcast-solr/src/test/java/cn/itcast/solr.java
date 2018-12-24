package cn.itcast;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-solr.xml")
public class solr {

    @Autowired
    private SolrTemplate solrTemplate;

    //新增更新
    @Test
    public void testAdd(){
        TbItem item = new TbItem();
        item.setId(100001172674L);
        item.setTitle("222 一加手机6T 8GB+128GB 墨岩黑 光感屏幕指纹 全面屏双摄游戏手机 全网通4G 双卡双待");
        item.setPrice(new BigDecimal(3599));
        item.setImage("https://item.jd.com/100001172674.html");
        item.setBrand("一加");
        item.setCategory("手机");

        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    /**
     * 根据主键删除
     */
    @Test
    public void deleteById(){
        solrTemplate.deleteById("100001172674");
        solrTemplate.commit();
    }
    /**
     *  根据条件删除
     */
    @Test
    public void deleteAll(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
