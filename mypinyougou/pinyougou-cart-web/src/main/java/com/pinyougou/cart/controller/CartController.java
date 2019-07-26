package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.entity.Cart;
import com.entity.Result;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 存入到cookie前提是没登录
 * （1）从cookie中取出购物车
 * （2）向购物车添加商品
 * （3）将购物车存入cookie
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    /**
     * 获取购物车列表
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //获取用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //由于在配置文件中配置了匿名访问，所以可以匿名，名字为anonymousUser
        if ("anonymousUser".equals(username)) {
            //匿名用户，说明没有登录，将数据保存进cookie
            String cartListString = CookieUtil.getCookieValue(request, "cartList", true);
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            return cookieCartList;
        }else {
            //已登录，将数据保存到Redis
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
            if (cartListFromRedis == null) {
                //没有数据,返回一个新的集合
                return new ArrayList<>();
            }else {
                //有数据，查询Cookie中的数据，合并
                String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
                if (StringUtils.isEmpty(cartListString)) {
                    //为空时赋初值
                    cartListString = "[]";
                }
                //不为空，将字符串转成对象
                List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
                if(cookieCartList.size()>0) {
                    //redis中购物车有数据
                    //合并
                    List<Cart> carts = cartService.mergeCartList(cookieCartList, cartListFromRedis);
                    //保存进Redis
                    cartService.saveCartListToRedis(username, carts);
                    //移除Cookie
                    CookieUtil.deleteCookie(request, response, "cartList");
                    return carts;
                }
                return cartListFromRedis;
            }
        }
    }

    /**
     * 添加商品到购物车（添加到已有的）
     * mvc配置文件配置也可以解决跨域
     *    <mvc:cors>
     *         <mvc:mapping path="/**" />
     *     </mvc:cors>
     * @return
     */
    //也可以使用注解解决跨域
    //@CrossOrigin(origins = "http://localhost:9105",allowCredentials="true")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request,
                                     HttpServletResponse response) {
        try {
            //解决跨域的方案，CORS,就是允许该域访问购物车，
            //response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
            //response.setHeader("Access-Control-Allow-Credentials","true");  //同时携带cookie

            //获取用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //由于在配置文件中配置了匿名访问，所以可以匿名，名字为anonymousUser
            if("anonymousUser".equals(name)) {
                //表示未登录是匿名用户
                //获取购物车列表
                String cartList = CookieUtil.getCookieValue(request, "cartList", true);
                if(StringUtils.isEmpty(cartList)){
                    cartList="[]";
                }
                List<Cart> cartListnew = JSON.parseArray(cartList, Cart.class);
                //进行添加，业务层已经写好方法了
                List<Cart> carts = cartService.addGoodsToCartList(cartListnew, itemId, num);
                //设置cookie
                CookieUtil.setCookie(request,response,"cartList",
                        JSON.toJSONString(carts),3600*24,true);
            }else {
                //从redis中获取数据
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
                List<Cart> carts = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                //保存到最新列表到redis中
                cartService.saveCartListToRedis(name,carts);
            }
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }




}
