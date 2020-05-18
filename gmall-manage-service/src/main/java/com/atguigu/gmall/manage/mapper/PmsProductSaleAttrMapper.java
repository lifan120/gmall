package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * PmsProductSaleAttrMapper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-06
 * @Description:
 */
public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
        List<PmsProductSaleAttr> getSpuSaleAttrListBySpuIdAndSkuId(@Param("spuId") String spuId,@Param("skuId") String skuId);
}
