package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.bean.OmsCartItem;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * OmsCartItemMapper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-16
 * @Description:
 */
public interface OmsCartItemMapper extends Mapper<OmsCartItem> {
    void deleteCarts(@Param("ids") String ids);
}
