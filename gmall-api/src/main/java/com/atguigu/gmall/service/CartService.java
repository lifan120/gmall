package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * CartService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-16
 * @Description:
 */
public interface CartService {
    //根据用户id与skuId查询数据库是否存在该项购物车数据
    OmsCartItem isCartExist(OmsCartItem omsCartItem);
    //修改购物车中商品数量
    void updateCart(OmsCartItem omsCartItemForDb);
    //添加商品进入购物车
    void addCart(OmsCartItem omsCartItem);
    //从db中获取购物车列表
    List<OmsCartItem> getCartList(String memberId);

    void deleteCartList(List<String> delCartList,String userId);
}
