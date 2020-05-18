package com.atguigu.gmall.user.serviceImpl;

import com.atguigu.gmall.user.bean.UmsMember;
import com.atguigu.gmall.user.mapper.UmsMemberMapper;
import com.atguigu.gmall.user.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UmsMemberServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-02
 * @Description:
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    @Autowired
    UmsMemberMapper umsMemberMapper;
    @Override
    public List<UmsMember> selectAllUser() {
        return umsMemberMapper.selectAllUser();
    }
}
