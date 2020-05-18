package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * SpuServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-06
 * @Description:
 */
@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Autowired
    PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;


    //根据三级分类id查询该分类下所有的spu
    @Override
    public List<PmsProductInfo> getSpuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        return pmsProductInfoMapper.select(pmsProductInfo);
    }
    //获取基础销售属性
    @Override
    public List<PmsBaseSaleAttr> getBaseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }
    //添加spu信息
    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        pmsProductInfoMapper.insert(pmsProductInfo);
        String spuId = pmsProductInfo.getId();
        //添加spu图片信息
        List<PmsProductImage> spuImageList = pmsProductInfo.getSpuImageList();
        for(PmsProductImage pmsProductImage : spuImageList){
            pmsProductImage.setProductId(spuId);
            pmsProductImageMapper.insert(pmsProductImage);
        }
        //添加spu销售属性信息
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        String spuSaleAttrId = "";
        for(PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList){
            pmsProductSaleAttr.setProductId(spuId);
            pmsProductSaleAttrMapper.insert(pmsProductSaleAttr);
            //添加spu销售属性值信息
            spuSaleAttrId = pmsProductSaleAttr.getSaleAttrId();
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            for(PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList){
                pmsProductSaleAttrValue.setProductId(spuId);
                pmsProductSaleAttrValue.setSaleAttrId(spuSaleAttrId);
                pmsProductSaleAttrValueMapper.insert(pmsProductSaleAttrValue);
            }
        }
    }
    //获取spu所有销售属性,通过xml文件查询 将当前skuId的销售属性标记出来
    @Override
    public List<PmsProductSaleAttr> getPmsProductSaleAttrList(String spuId,String skuId) {
       List<PmsProductSaleAttr> pmsProductSaleAttrList =  pmsProductSaleAttrMapper.getSpuSaleAttrListBySpuIdAndSkuId(spuId,skuId);
        return pmsProductSaleAttrList;
    }
    //获取spu所有销售属性,根据spuId查询
    @Override
    public List<PmsProductSaleAttr> getPmsProductSaleAttrList(String spuId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        return pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
    }

    //获取spu的所有图片信息
    @Override
    public List<PmsProductImage> getImgList(String spuId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);
        List<PmsProductImage> ImageList = pmsProductImageMapper.select(pmsProductImage);
        return ImageList;
    }
}
