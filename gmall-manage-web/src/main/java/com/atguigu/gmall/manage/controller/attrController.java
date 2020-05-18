package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.service.AttrService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * attrController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-04
 * @Description:
 */
//平台销售属性管理
@RestController
@CrossOrigin
public class attrController {
    @Reference
    AttrService attrService;

    //获取所有三级分类对应的平台销售属性
    @RequestMapping("/attrInfoList")
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id){
        return attrService.getAttrInfoList(catalog3Id);
    }

    //添加三级分类对应的平台销售信息
    @RequestMapping("/saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo){
        attrService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }
}
