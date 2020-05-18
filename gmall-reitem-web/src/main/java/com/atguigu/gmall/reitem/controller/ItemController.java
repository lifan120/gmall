package com.atguigu.gmall.reitem.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-07
 * @Description:
 */
@Controller
public class ItemController {
    @Reference
    SkuService skuService;
    @Reference
    SpuService spuService;
    //第二种方式利用静态json数据存储spu的所有sku信息，然后通过ajax请求根据spuId请求json数据然后再进行比对就不用查询数据库了
    @RequestMapping("{skuId}.html1")
    public String item1(@PathVariable("skuId") String skuId, Map map){
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);
        String spuId = skuInfo.getSpuId();
        List<PmsProductSaleAttr> spuSaleAttrList = spuService.getPmsProductSaleAttrList(spuId,skuId);
        map.put("skuInfo",skuInfo);
        map.put("spuSaleAttrListCheckBySku",spuSaleAttrList);
        map.put("spuId",spuId);
        return "item";
    }


    //查看商品详情(也就是spu详情)，通过sku的id查看，将spu的销售属性与值和要查看的sku信息传递过去
    @RequestMapping("{skuId}.html")
    public String item(@PathVariable("skuId") String skuId, Map map){
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);
        String spuId = skuInfo.getSpuId();
        //根据spuId与skuId获得所有spu销售属性并将当前的sku销售信息标记
        List<PmsProductSaleAttr> spuSaleAttrList = spuService.getPmsProductSaleAttrList(spuId,skuId);
        map.put("skuInfo",skuInfo);
        map.put("spuSaleAttrListCheckBySku",spuSaleAttrList);
        //根据spuID查询该spu下已有的sku的销售属性值id,
        List<PmsSkuInfo> skuInfoList = skuService.getAllSkuInfoBySpuId(spuId);
        //将其组成json格式{所有sku的attr_value_id用|分割 : skuID}，切换销售属性时不需要再向数据库查skuid
        String k = "";
        String v = "";
        Map jsonMap = new HashMap();
        for(PmsSkuInfo pmsSkuInfo : skuInfoList){
            v = pmsSkuInfo.getId();
            List<PmsSkuSaleAttrValue> skuAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
            for(PmsSkuSaleAttrValue saleAttrValue : skuAttrValueList){
                k = k + "|"+saleAttrValue.getSaleAttrValueId();
            }
            jsonMap.put(k,v);
        }
        String jsonString = JSON.toJSONString(jsonMap);
        map.put("skuSaleAttrValueJsonString",jsonString);
        return "item";
    }
}
