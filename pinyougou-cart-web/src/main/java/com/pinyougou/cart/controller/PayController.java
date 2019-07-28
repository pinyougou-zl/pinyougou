package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.Result;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;


    /**
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);
        //IdWorker idWorker = new IdWorker(0, 1);
        if(tbPayLog != null) {
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(),tbPayLog.getTotalFee()+"");
        }
        return null;
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        //加个需求，时间限制
        int count = 0;

        while (true) {
            //调用查询接口
            Map map = weixinPayService.queryPayStatus(out_trade_no);

            if(map == null) {
                //出错
                result = new Result(false,"支付出错");
                break;  //跳出循环
            }

            if("SUCCESS".equals(map.get("trade_state"))) {
                //成功
                result = new Result(true,"支付成功");
                orderService.updateorderStatus(out_trade_no, (String) map.get("transaction_id"));
                break;
            }
            count++;
            if(count>=100) {
                result = new Result(false,"支付超时");
                break;
            }

            try {
                Thread.sleep(3000);  //睡眠3秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
