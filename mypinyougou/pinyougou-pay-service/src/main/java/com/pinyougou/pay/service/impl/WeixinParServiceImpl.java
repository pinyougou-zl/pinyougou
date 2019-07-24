package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class WeixinParServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    /**
     * 生成二维码
     * @param out_trade_no  订单号
     * @param total_fee     总价格
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        Map<String,String> param = new HashMap<>();  //放入集合
        param.put("appid",appid);   //公众号
        param.put("mch_id",partner);  //商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());  //随机字符串
        param.put("body","品优购"); //商品描述
        param.put("out_trade_no",out_trade_no);  //商品订单号
        param.put("total_fee",total_fee);  //总金额（注意单位是分）
        param.put("spbill_create_ip","127.0.0.1");  //IP
        param.put("notify_url","http://test.itcast.cn");  //回调地址，随便写
        param.put("trade_type","NATIVE");  //交易类型--》二维码

        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println(xmlParam);
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);  //设置协议
            client.setXmlParam(xmlParam);
            client.post();

            //3.获得结果
            String result = client.getContent();
            System.out.println(result);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            Map<String,String> map = new HashMap<>();
            map.put("code_url", resultMap.get("code_url"));//支付地址
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no",out_trade_no);//订单号
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 检查支付状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map param = new HashMap();
        param.put("appid",appid);  //公众号id
        param.put("mch_id",partner);   //商户号
        param.put("out_trade_no",out_trade_no);  //订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());  //随机数字
        //支付连接的接口
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient client = new HttpClient(url);  //远程调用该接口
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();  //模拟浏览器发送请求
            String result = client.getContent();  //获得结果
            Map<String, String> map = WXPayUtil.xmlToMap(result);
            System.out.println(map);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        try {
            //参数设置
            Map<String,String> paramMap = new HashMap<String,String>();
            paramMap.put("appid",appid); //应用ID
            paramMap.put("mch_id",partner);    //商户编号
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符
            paramMap.put("out_trade_no",out_trade_no);   //商家的唯一编号

            //将Map数据转成XML字符
            String xmlParam = WXPayUtil.generateSignedXml(paramMap,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");

            //确定url
            String url = "https://api.mch.weixin.qq.com/pay/closeorder";

            //发送请求
            HttpClient httpClient = new HttpClient(url);
            //https
            httpClient.setHttps(true);
            //提交参数
            httpClient.setXmlParam(xmlParam);

            //提交
            httpClient.post();

            //获取返回数据
            String content = httpClient.getContent();

            //将返回数据解析成Map
            return  WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }
}
