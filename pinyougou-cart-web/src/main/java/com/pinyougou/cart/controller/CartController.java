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
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //由于在配置文件中配置了匿名访问，所以可以匿名，名字为anonymousUser
        if("anonymousUser".equals(name)) {
            //表示未登录是匿名用户
            //从cookie中查询购物车列表
            String castListString = CookieUtil.getCookieValue(request, "cartList", true);
            //如果没有置为空
            if( StringUtils.isEmpty(castListString) ) {
                castListString = "[]";
            }
            //如果有直接转换成集合，返回
            List<Cart> carts = JSON.parseArray(castListString, Cart.class);
            System.out.println("cookie"+carts);
            return carts;
        }else {
            //已登录
            //从redis中取出数据
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);

            //合并购物车的数据
            //1.获取cookie中的购物车的数据
            String cartListstring = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if(StringUtils.isEmpty(cartListstring)) {
                cartListstring = "[]";
            }
            List<Cart> carts = JSON.parseArray(cartListstring, Cart.class);

            //2、获取redis中的购物车数据
            if(cartListFromRedis == null) {
                cartListFromRedis = new ArrayList<>();
            }
            //3、进行合并,返回一个最新的购物车数据
            List<Cart> cartsnew = cartService.mergeCartList(carts, cartListFromRedis);
            //4、将最新的数据重新设置会redis中
            cartService.saveCartListToRedis(name,cartsnew);
            //5、cookie中的购物车清除
            CookieUtil.deleteCookie(request,response,"cartList");
            //返回最新
            return cartsnew;
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
            response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
            response.setHeader("Access-Control-Allow-Credentials","true");  //同时携带cookie


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
