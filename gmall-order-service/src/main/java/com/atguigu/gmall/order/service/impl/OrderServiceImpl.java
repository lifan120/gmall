package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.UUID;

/**
 * OrderService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-22
 * @Description:
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Override
    public String createTradeCode(String userId) {
        String uuid = UUID.randomUUID().toString();
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            jedis.setex("user:" + userId + ":tradeCode", 60 * 30, uuid);
        }catch (Exception e){

        }finally {
            jedis.close();
        }
        return uuid;
    }

    @Override
    public boolean checkTradeCode(String userId, String tradeCode) {
        Jedis jedis = null;
        try{
            jedis = redisUtil.getJedis();
            String tradeCodeForRedis = jedis.get("user:" + userId + ":tradeCode");
            if (tradeCode.equals(tradeCodeForRedis)) {
                jedis.del("user:" + userId + ":tradeCode");//删除交易码
                return true;
            }
        }catch (Exception e){

        }finally {
            jedis.close();
        }
        return false;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        omsOrderMapper.insertSelective(omsOrder);
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for(OmsOrderItem omsOrderItem : omsOrderItems){
            omsOrderItem.setOrderId(omsOrder.getId());
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
    }

    @Override
    public OmsOrder getOrderByTradeNo(String out_trade_no) {
        OmsOrder omsOrderForDb = omsOrderMapper.selectOrderByOutTradeCode(out_trade_no);
        return omsOrderForDb;
    }

    @Override
    public void updateOrderByOutTradeNo(String out_trade_no, String pay_status) {
        //更新订单的支付状态
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        omsOrder.setStatus(pay_status);
    }
}
