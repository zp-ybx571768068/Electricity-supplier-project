package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zp
 */
@RequestMapping("/user")
@RestController
public class UserController {

    @GetMapping("getUsernmae")
    public String getUsernmae(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userName;
    }
}
