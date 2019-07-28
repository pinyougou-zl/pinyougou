package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.entity.Cart;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    //注入redis
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //2.获取商家ID  sellerId
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
            //遍历已有购物车中的列表,在这我们写一个方法
        Cart cart = findCartBySellerId(sellerId,cartList);
        //4.如果购物车列表中不存在该商家的购物车
        if(cart == null) {
            //4.1 新建购物车对象
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller()); //店铺名
            //明细列表
            //4.2 将新建的购物车对象添加到购物车列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem orderItemNew = new TbOrderItem();
            //补充属性
            orderItemNew.setItemId(itemId);
            orderItemNew.setGoodsId(item.getGoodsId());
            orderItemNew.setTitle(item.getTitle());
            orderItemNew.setPrice(item.getPrice());
            //传递过来的购买数量
            orderItemNew.setNum(num);
            //计算价格
            double v = num*item.getPrice().doubleValue();
            orderItemNew.setTotalFee(new BigDecimal(v));
            orderItemNew.setPicPath(item.getImage());  //商品的图片路径
            orderItemNew.setSellerId(sellerId);
            //放入集合
            orderItemList.add(orderItemNew);
            //放入Cart对象
            cart.setOrderItemList(orderItemList);
            //所有数据都放好了，存入原有的集合中
            cartList.add(cart);
        }else {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();  //明细列表
            TbOrderItem orderItem = findOrderItemByItemId(itemId,orderItemList);
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            if(orderItem != null ) {
                //5.1. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);  //数量相加
                //重新计算金额
                double v = orderItem.getNum()*orderItem.getPrice().doubleValue();
                orderItem.setTotalFee(new BigDecimal(v)); //更新设置
                //判断如果商品的数量为0，表示不买了，就要删除商品
                if(orderItem.getNum() == 0) {
                    orderItemList.remove(orderItem);
                }
                //如果长度为空，用户没购买该商家的商品就直接删除对象
                if(orderItemList.size() == 0) {
                    cartList.remove(cart);  //商家也删除了,在购物车列表里面删除
                }
            }else {
                //5.2. 如果没有，新增购物车明细
                TbOrderItem orderItemnew = new TbOrderItem();
                //重新设置属性
                orderItemnew.setItemId(itemId);
                orderItemnew.setGoodsId(item.getGoodsId());
                orderItemnew.setTitle(item.getTitle());
                orderItemnew.setPrice(item.getPrice());
                orderItemnew.setNum(num);  //就是传过来的数量
                //重新设置金额
                double v = num*item.getPrice().doubleValue();
                orderItemnew.setTotalFee(new BigDecimal(v));
                orderItemnew.setPicPath(item.getImage());
                orderItemnew.setSellerId(sellerId);
                orderItemList.add(orderItemnew);
            }
        }
        return cartList;
    }

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
    }

    /**
     * 将购物车保存到redis中
     * @param username
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * 两者合并，存入到redis中
     * @param cookieList
     * @param redisList
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
        for (Cart cart : cookieList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                redisList = addGoodsToCartList(redisList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisList;
    }

    /**
     * 该方法是判断明细列表是否存在该商品, 也就是说，我们所添加商品，购物车有该商品的商家，
     * 判断里面商品详细列表是否有该商品，如果有就添加，修改数量与价格，如果没有根据传来的数据
     * 新建商品信息，放入到详细列表中
     * @param itemId
     * @param orderItemList
     * @return
     */
    private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue() == itemId) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 该方法是判断所添加的商品在原本的购物车有没有
     * @param sellerId
     * @param cartList
     * @return
     */
    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        for (Cart cart : cartList) {
            String sellerId1 = cart.getSellerId();
            if(sellerId.equals(sellerId1)) {
                return cart;
            }
        }
        return null;
    }
}
