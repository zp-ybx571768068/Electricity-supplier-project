package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zp
 */
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String REDIS_CART_LIST = "CART_LIST";
    /**
     * 1、验证商品是否存在，商品的启用状态是否启用
     * 2、如果该商品对应的商家不存在在购物车列表中；则重新加商家及其对应的商品
     * 3、如果该商品对应的商家存在在购物车列表中；那么判断商品是否存在若是则购买数量叠加，否则新加入商品到该商家
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1、验证商品是否存在，商品的启用状态是否启用
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("商品状态不合法");
        }
        
        String sellerId = item.getSellerId();
        Cart cart= findCarBySellerId(cartList,sellerId);
        if (cart == null) {
            if (num > 0){
            //2、如果该商品对应的商家不存在在购物车列表中；则重新加商家及其对应的商品
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItem = createOrderItem(item,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);
            }else {
               throw new RuntimeException("购买数量非法");
            }
        }else {
            //3、如果该商品对应的商家存在在购物车列表中；那么判断商品是否存在若是则购买数量叠加，否则新加入商品到该商家
            TbOrderItem orderItem = findOrerItemByItemId(cart.getOrderItemList(),itemId);
            if (orderItem != null){
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //说明购买数量小于0，则需要将商品删除
                if (orderItem.getNum() == 0  || orderItem.getNum() < 0 ){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果删除商品后购物车的明细没有任何商品则需要将购物车也删除
                if (cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }else {
                if (num > 0){
                    orderItem = createOrderItem(item,num);
                    cart.getOrderItemList().add(orderItem);
                }else {
                    throw new RuntimeException("购买数量不合法");
                }
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> findCartListByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (cartList!=null){
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListByUsername(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username,newCartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        //任何一个集合合并都可以；商品不存在则新增，存在则购买数量叠加
        for (Cart cart: cookieCartList){
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList){
                addItemToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     *  在购物车商品明细表里面根据商品id查找对应的明细
     * @param orderItemList 购物车商品明细列表
     * @param itemId 商品id
     * @return 购物车明细
     */
    private TbOrderItem findOrerItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList != null && orderItemList.size() > 0){
            for (TbOrderItem orderItem : orderItemList){
                if (itemId.equals(orderItem.getItemId())){
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     *  构造购物车明细
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setPicPath(item.getImage());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     *  根据商家id在购物车列表中查询购物车
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return 购物车
     */
    private Cart findCarBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0){
            for (Cart cart : cartList){
                if (sellerId.equals(cart.getSellerId())){
                    return cart;
                }
            }
        }
        return null;
    }
}
