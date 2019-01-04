package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

/**
 * @author zp
 */
public interface CartService {
    /**
     * 根据商品 id 查询商品和购买数量加入到 cartList
     * @param cartList 购物车列表
     * @param itemId 商品 id
     * @param num 购买数量
     * @return 购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     *  根据用户id查询redis中对应的购物车列表
     * @param username
     * @return
     */
    List<Cart> findCartListByUsername(String username);

    /**
     *  将用户对应的购物车列表保存到redis中
     * @param newCartList 购物车列表
     * @param username 用户id
     */
    void saveCartListByUsername(List<Cart> newCartList, String username);

    /**
     *  合并两个购物车列表
     * @param cookieCartList cookie中的购物车列表
     * @param redisCartList redis中的购物车列表
     * @return 合并之后的购物车列表
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);
}
