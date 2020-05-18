package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * UserService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-20
 * @Description:
 */
public interface UserService {
    //校验登录者
    UmsMember checkUmsMember(UmsMember umsMember);
    //根据用户id查询用户的所有收货地址
    List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressByUserId(String userId);

    UmsMemberReceiveAddress getUmsMemberReceiveAddressByAddressId(String addressId);
}
