package com.pinyougou.cart.service;

import com.entity.Cart;

import java.util.List;

public interface CartService {
    /**
     * 向已有的购物车添加商品,在原有的商品上添加  数量加一
     * cartList  已有的购物车
     * itemId  商品的id    根据他来查询商品
     * num   购买的数量
     */
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis中
     * @param username
     * @param cartList
     */
    public void saveCartListToRedis(String username,List<Cart> cartList);

    /**
     * 两者合并，全部存入到redis中
     * @param cookieList
     * @param redisList
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cookieList,List<Cart> redisList);
}
