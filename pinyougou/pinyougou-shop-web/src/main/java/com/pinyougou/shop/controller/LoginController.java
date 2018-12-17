package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zp
 */
@RequestMapping("/login")
@RestController
public class LoginController {

    /**
     *  使用 SecurityContextHolder API 获取用户名并返回
     * @return
     */
    @GetMapping("/getUsername")
    public Map<String,String> getUsername(){
        Map<String,String> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);

        return map;
    }
}
