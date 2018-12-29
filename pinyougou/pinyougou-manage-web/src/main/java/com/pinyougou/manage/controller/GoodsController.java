package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;
import java.util.List;

/**
 * @author zp
 */
@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jsmTemplate;

    @Autowired
    private ActiveMQQueue itemSolrQueue;

    @Autowired
    private ActiveMQQueue itemSolrDeleteQueue;

    @Autowired
    private ActiveMQTopic itemTopic;

    @Autowired

    private ActiveMQTopic itemDeleteTopic;


    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {

        Result result = goodsService.updateStatus(ids, status);
        //如果审核通过商品才更新solr索引库
        if ("2".equals(status)) {
            //查询需要更新的列表
            List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids, "1");
            jsmTemplate.send(itemSolrQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage();

                    textMessage.setText(JSON.toJSONString(itemList));

                    return textMessage;
                }
            });
            sendMQMsg(itemTopic, ids);
        }
        return result;
    }

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbGoods goods) {
        try {
            goodsService.add(goods);
            return Result.success("商家注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("商家注册失败");
    }

    @GetMapping("/findOne")
    public TbGoods findOne(Long id) {
        return goodsService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbGoods goods) {
        try {
            goodsService.update(goods);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);
            //删除solr中对应的商品数据
            sendMQMsg(itemSolrDeleteQueue, ids);
            //发送商品删除的订阅信息
            sendMQMsg(itemDeleteTopic, ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    /**
     * 发送消息到activeMQ
     *
     * @param destination 发送模式
     * @param ids         商品Id集合
     */
    private void sendMQMsg(Destination destination, Long[] ids) {

        try {
            jsmTemplate.send(destination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });
        } catch (JmsException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分页查询列表
     *
     * @param goods 查询条件
     * @param page  页号
     * @param rows  每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.search(page, rows, goods);
    }

}
