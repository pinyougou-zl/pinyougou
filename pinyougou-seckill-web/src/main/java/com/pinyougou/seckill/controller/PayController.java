package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.entity.Result;
import com.pinyougou.SeckillOrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
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
    private SeckillOrderService seckillOrderService;


    /**
     * 生成二维码
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder order = seckillOrderService.getUserOrderStatus(userId);
        //IdWorker idWorker = new IdWorker(0, 1);
        if(order != null) {
            //获得金额
            double v = order.getMoney().doubleValue()*100;
            long x = (long)v;
            return weixinPayService.createNative(order.getId()+"",x+"");
        }
        return null;
    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {
        Result result = null;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            int count = 0;
            //调用用支付服务
            /**
             * 此时该方法 是后台线程循环进行状态查询，压力较大，应当在页面定时发送请求来查询，这样的解决方案比此处的解决方案要好。此处不再实现。
             */
            while (true) {
                Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
                count++;

                if(count>=100) {
                    result=new Result(false,"支付超时");
                    //关闭微信订单
                    Map map1 = weixinPayService.closePay(out_trade_no);
                    if("ORDERPAID".equals(map.get("err_code"))) {
                        //已经支付更新入库
                        seckillOrderService.updateOrderStatus(map.get("transaction_id"),userId);
                    }else if("SUCCESS".equals(map.get("result_code")) ||
                            "ORDERCLOSED".equals(map.get("err_code"))) {
                        //删除预订单
                        seckillOrderService.deleteOrder(userId);
                    }else {
                        System.out.println("由于微信端错误");
                    }
                    break;
                }
                Thread.sleep(3000);

                //如果超时5分钟就直接退出
                if("SUCCESS".equals(map.get("trade_state"))) {
                    //支付成功
                    result = new Result(true,"支付成功");
                    seckillOrderService.updateOrderStatus(map.get("transaction_id"),userId);
                    break;
                }
            }
            //返回结果
            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }


    }
}
