package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SkuController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-07
 * @Description:
 */
@RestController
@CrossOrigin
public class SkuController {
    @Reference
    SkuService skuService;
    //保存sku信息
    @RequestMapping("/saveSkuInfo")
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        skuService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }


}
