package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.user.bean.UmsMember;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * UserMemberMapper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-02
 * @Description:
 */
public interface UmsMemberMapper extends Mapper<UmsMember> {
    public List<UmsMember> selectAllUser();
}
