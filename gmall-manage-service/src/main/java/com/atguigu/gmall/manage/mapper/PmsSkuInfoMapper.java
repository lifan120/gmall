package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.PmsSkuInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * PmsSkuInfoMapper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-07
 * @Description:
 */
public interface PmsSkuInfoMapper extends Mapper<PmsSkuInfo> {
    List<PmsSkuInfo> selectAllSkuInfoBySpuId(@Param("spuId") String spuId);
}
