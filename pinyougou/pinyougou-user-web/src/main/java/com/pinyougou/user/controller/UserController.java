package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.pinyougou.common.util.PhoneFormatCheckUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    /**
     *  获取当前登陆用户信息
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String,Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }

    /**
     *  user/sendSmsCode.do?phone=" +phone 发送短信验证码
     * @param phone 用户的手机号码
     * @return
     */
    @GetMapping("sendSmsCode")
    public Result sendSmsCode(String phone){
        try {
            if(PhoneFormatCheckUtils.isPhoneLegal(phone)){
                userService.sendSmsCode(phone);
                return Result.success("验证码发送成功");
            }else {
                return Result.error("手机号格式错误，请重新输入");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return Result.error("验证码发送失败");
    }
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbUser user,String smsCode) {
        try {
            if (userService.checkSmsCode(user.getPhone(),smsCode)){
                user.setCreated(new Date());
                user.setUpdated(user.getCreated());
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                userService.add(user);
                return Result.success("注册成功");
            }else {
                return Result.error("验证码错误，注册失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("注册失败");
    }

    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteById(ids);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error("删除失败");
    }

    /**
     * 分页查询列表
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbUser user, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return userService.search(page, rows, user);
    }

}
