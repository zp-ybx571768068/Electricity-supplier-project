package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    /**
     *  user/sendSmsCode.do?phone=" +phone 发送短信验证码
     * @param phone 用户的手机号码
     * @return
     */
    void sendSmsCode(String phone);

    /**
     *  校验用户验证码
     * @param phone
     * @param smsCode
     * @return
     */
    boolean checkSmsCode(String phone, String smsCode);

}