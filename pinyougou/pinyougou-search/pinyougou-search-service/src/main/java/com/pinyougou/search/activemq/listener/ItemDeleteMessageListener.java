package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author  zp
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message, Session session) throws JMSException {

        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] ids = (Long[]) objectMessage.getObject();
        itemSearchService.deleteItemByGoodsIdList(Arrays.asList(ids));
    }
}
