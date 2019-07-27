package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface WeixinPayService {
    /**
     * 生成微信二维码
     * @param out_trade_no  订单号
     * @param total_fee     总价格
     * @return
     */
    public Map createNative(String out_trade_no,String total_fee);

    /**
     * 检查支付状态
     * @param out_trade_no
     * @return
     */
    public Map queryPayStatus(String out_trade_no);

    /**
     * 关闭支付
     * @param out_trade_no
     * @return
     */
    public Map closePay(String out_trade_no);

}
