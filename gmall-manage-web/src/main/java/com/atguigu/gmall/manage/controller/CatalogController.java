package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsBaseCatalog2;
import com.atguigu.gmall.bean.PmsBaseCatalog3;
import com.atguigu.gmall.service.CatalogService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CatalogController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-03
 * @Description:
 */
//平台分类信息管理
@RestController
@CrossOrigin
public class CatalogController {
    @Reference
    CatalogService catalogService;

    //获取三级分类
    @RequestMapping("/getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id){
        return catalogService.getCatalog3(catalog2Id);
    }

    //获取二级分类
    @RequestMapping("/getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id){
        return catalogService.getCatalog2(catalog1Id);
    }
    //获取一级分类
    @RequestMapping("/getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1(){
        return catalogService.getCatalog1();
    }
}
