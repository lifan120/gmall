package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsBaseCatalog2;
import com.atguigu.gmall.bean.PmsBaseCatalog3;

import java.util.List;

/**
 * CatalogService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-03
 * @Description:
 */
//平台分类信息管理
public interface CatalogService {
    String hello();
    //获取一级分类
    List<PmsBaseCatalog1> getCatalog1();
    //获取二级分类
    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);
    //获取三级分类
    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
