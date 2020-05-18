package com.atguigu.gmall.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.pay.config.AlipayConfig;

import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * PayController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-23
 * @Description:
 */
@Controller
public class PayController {
    @Reference
    OrderService orderService;
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    PayService payService;

    @LoginRequired(isNeedToken = true)
    @RequestMapping("/index")
    public String index(String out_trade_no, HttpServletRequest request, Map map){
        String nickName = (String)request.getAttribute("nickName");
        //根据外部订单号返回订单的总金额
        OmsOrder omsOrder = orderService.getOrderByTradeNo(out_trade_no);
        map.put("nickName",nickName);
        map.put("out_trade_no",out_trade_no);
        map.put("totalAmount",omsOrder.getTotalAmount());
        return "index";
    }
    @LoginRequired(isNeedToken = true)
    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipaySumbit(String out_trade_no,String totalAmount){
        AlipayTradePagePayRequest alipayRequest =  new  AlipayTradePagePayRequest(); //创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl( AlipayConfig.notify_payment_url); //在公共参数中设置回跳和通知地址
        OmsOrder orderByTradeNo = orderService.getOrderByTradeNo(out_trade_no);//支付封装参数
        Map<String,Object> map = new HashMap();
        map.put("out_trade_no",out_trade_no);
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("total_amount","0.01");
        map.put("subject",orderByTradeNo.getOmsOrderItems().get(0).getProductName());
        alipayRequest.setBizContent(JSON.toJSONString(map));
        String form="";
        try{
           form = alipayClient.pageExecute(alipayRequest).getBody();
        }catch (AlipayApiException e){
            e.printStackTrace();
        }
        //保存付款信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderId(orderByTradeNo.getId());
        paymentInfo.setOrderSn(orderByTradeNo.getOrderSn());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setSubject(orderByTradeNo.getOmsOrderItems().get(0).getProductName());
        paymentInfo.setPaymentStatus("0");
        payService.savePayMentInfo(paymentInfo);

        //提交后开启一个延时队列调用支付宝接口检查用户是否支付
        payService.checkPayStatusMq(out_trade_no,7);
        return form;
    }
    //支付宝支付后的回调函数
    @RequestMapping("alipay/callback/return")
    public String callBack(HttpServletRequest request,Map map){
        //修改支付信息
        PaymentInfo paymentInfo = new PaymentInfo();
        String trade_no = request.getParameter("trade_no");
        String app_id = request.getParameter("app_id");
        String out_trade_no = request.getParameter("out_trade_no");
        String out_biz_no = request.getParameter("out_biz_no");//没有值
        String total_amount = request.getParameter("total_amount");
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setPaymentStatus("1");

        payService.updatePayMentInfo(paymentInfo);
        //订单服务
        //物流服务
        //其它服务

        return "finish";
    }

}
