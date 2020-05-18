package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.user.bean.UmsMember;
import com.atguigu.gmall.user.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * UmsMemberController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-02
 * @Description:
 */
@Controller
public class UmsMemberController {
    @Autowired
    UmsMemberService umsMemberService;
    @RequestMapping("/hello")
    @ResponseBody
    public List<UmsMember> hello(){

        return umsMemberService.selectAllUser();
    }
}
