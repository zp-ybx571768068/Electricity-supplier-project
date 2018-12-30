package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zp
 */
@RequestMapping("/mq")
@RestController
public class MqController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     *
     */
    @GetMapping("/sendMsg")
    public String sendMessage(){
        Map<String,String> map = new HashMap<>();
        map.put("name","zp");
        map.put("age","20");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue",map);

        return "发送成功";
    }

}
