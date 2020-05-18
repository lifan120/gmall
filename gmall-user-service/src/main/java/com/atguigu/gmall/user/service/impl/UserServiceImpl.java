package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UmsMemberMapper;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * UserServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-20
 * @Description:
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UmsMemberMapper umsMemberMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    //根据username与password检验是否存在用户
    @Override
    public UmsMember checkUmsMember(UmsMember umsMember) {
        UmsMember umsMemberForDb = null;
        UmsMember umsMemberParam = new UmsMember();
        umsMemberParam.setUsername(umsMember.getUsername());
        umsMemberParam.setPassword(umsMember.getPassword());
        umsMemberForDb = umsMemberMapper.selectOne(umsMemberParam);
        return umsMemberForDb;
    }

    @Override
    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddressByUserId(String userId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(userId);
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return umsMemberReceiveAddressList;
    }

    @Override
    public UmsMemberReceiveAddress getUmsMemberReceiveAddressByAddressId(String addressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(addressId);
        UmsMemberReceiveAddress umsMemberReceiveAddressForDb = umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return umsMemberReceiveAddressForDb;
    }


}
