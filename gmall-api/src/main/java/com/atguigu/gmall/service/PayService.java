package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

/**
 * PayService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-23
 * @Description:
 */
public interface PayService {
    void savePayMentInfo(PaymentInfo paymentInfo);

    void updatePayMentInfo(PaymentInfo paymentInfo);

    void checkPayStatusMq(String out_trade_no,int count);

    PaymentInfo checkPayStatus(String out_trade_no);

    PaymentInfo checkPayStatusForAli(String out_trade_no);
    void sendPaySuccessMq(PaymentInfo paymentInfo);
}
