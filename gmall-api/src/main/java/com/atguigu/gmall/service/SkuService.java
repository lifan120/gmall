package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsSkuInfo;

import java.util.List;

/**
 * SkuService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-07
 * @Description:
 */
public interface SkuService {

    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getSkuById(String skuId);

    List<PmsSkuInfo> getAllSkuInfoBySpuId(String spuId);

    List<PmsSkuInfo> getAllskuInfo();
}
