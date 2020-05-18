package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.bean.OmsOrder;
import org.apache.ibatis.annotations.Param;
import org.springframework.core.annotation.Order;
import tk.mybatis.mapper.common.Mapper;

/**
 * OmsOrderMaper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-22
 * @Description:
 */
public interface OmsOrderMapper extends Mapper<OmsOrder> {
    OmsOrder selectOrderByOutTradeCode(@Param("outTradeNo") String outTradeCode);
}
