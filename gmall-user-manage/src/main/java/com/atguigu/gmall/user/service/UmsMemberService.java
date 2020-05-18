package com.atguigu.gmall.user.service;

import com.atguigu.gmall.user.bean.UmsMember;

import java.util.List;

/**
 * UmsMemberService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-02
 * @Description:
 */
public interface UmsMemberService {

    List<UmsMember> selectAllUser();
}
