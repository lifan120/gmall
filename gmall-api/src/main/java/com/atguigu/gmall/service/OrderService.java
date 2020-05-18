package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsOrder;

/**
 * OrderService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-22
 * @Description:
 */
public interface OrderService {
    String createTradeCode(String userId);

    boolean checkTradeCode(String userId, String tradeCode);

    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByTradeNo(String out_trade_no);

    void updateOrderByOutTradeNo(String out_trade_no, String pay_status);
}
