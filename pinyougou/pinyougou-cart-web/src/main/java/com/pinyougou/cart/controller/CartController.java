package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pinyougou.common.util.CookieUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zp
 */
@RequestMapping("/cart")
@RestController
public class CartController {

    /**
     *  cookie中购物车列表名称
     */
    private static final String COOK_CART_LIST="PYG_CART_LIST";
    /**
     * 匿名账户名称
     */
    private static final String ANONYMOUS_ACCOUNT = "anonymousUser";
    /**
     * 购物车在cookie中的最大生存时间；1天
     */
    private static final int COOKIE_CART_MAX_AGE = 3600*24;
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;


    /**
     *  实现登陆或未登陆情况下将商品加入购物车列表
     * @param itemId 商品id
     * @param num 购买数量
     * @return 操作结果
     */
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        try {
            //获取用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将商品加入到购物车列表
            List<Cart> newCartList = cartService.addItemToCartList(cartList,itemId,num);
            //未登陆，将商品加入写回到cookie中
            if (ANONYMOUS_ACCOUNT.equals(username)){
                String cartListJsonStr = JSONArray.toJSONString(newCartList);
                CookieUtils.setCookie(request,response,COOK_CART_LIST
                        ,cartListJsonStr,COOKIE_CART_MAX_AGE,true);
            }else {
                //已登陆写入redis中
                cartService.saveCartListByUsername(newCartList,username);
            }
        return Result.success("加入购物车成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.error("加入购物车失败");
    }
    /**
     *  获取购物车列表输入，如果登陆了从redis中获取，如果没登陆从cookie中获取
     * @return 购物车列表数据
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        //判断用户是否已经登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            String cartListJsonStr = CookieUtils.getCookieValue(request,COOK_CART_LIST,true);
            List<Cart> cookieCartList = new ArrayList<>();
            if (!StringUtils.isEmpty(cartListJsonStr)){
                cookieCartList = JSONArray.parseArray(cartListJsonStr,Cart.class);
            }
            //如果用户名是anonymousUser说明是匿名登陆
            if (ANONYMOUS_ACCOUNT.equals(username)){
                //未登陆从cookie中获取数据
                return cookieCartList;
            }else {
                //已登陆从redis中获取数据
                List<Cart> redisCartList = cartService.findCartListByUsername(username);
                //合并购物车列表
                if (cookieCartList != null && cookieCartList.size() > 0 ){
                    //1.将cookie中的购物车与redis中的购物车列表进行合并到一个新的列表
                    redisCartList = cartService.mergeCartList(cookieCartList,redisCartList);
                    //将合并的购物车列表协会redis；
                    cartService.saveCartListByUsername(redisCartList,username);
                    //删除cookie中的购物车
                    CookieUtils.deleteCookie(request,response,COOK_CART_LIST);
                }
                return redisCartList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  获取当前用户的登陆信息
     */
    @GetMapping("/getUsername")
    public Map<String,Object> getUsername(){
        Map<String, Object> map = new HashMap<>();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果是匿名登陆，则用户名为anonymousUser
        map.put("username",userName);
        return map;
    }


}
