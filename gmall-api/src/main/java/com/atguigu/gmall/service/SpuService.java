package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;

import java.util.List;

/**
 * SpuService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-06
 * @Description:
 */
public interface SpuService {

    //根据三级分类id查询该分类下所有的spu
    List<PmsProductInfo> getSpuList(String catalog3Id);
    //获取基础销售属性
    List<PmsBaseSaleAttr> getBaseSaleAttrList();
    //添加spu信息
    void saveSpuInfo(PmsProductInfo pmsProductInfo);
    //根据spuId与skuId获得所有spu销售信息并将当前的sku销售信息标记
    List<PmsProductSaleAttr> getPmsProductSaleAttrList(String spuId,String skuId);
    //根据spuId获得所有spu销售信息
    List<PmsProductSaleAttr> getPmsProductSaleAttrList(String spuId);
    //获得所有spu图片信息
    List<PmsProductImage> getImgList(String spuId);
}
