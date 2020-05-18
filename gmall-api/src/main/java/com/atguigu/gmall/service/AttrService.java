package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;

import java.util.List;
import java.util.Set;

/**
 * AttrService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-04
 * @Description:
 */
//平台销售属性管理
public interface AttrService {
    //获取所有三级属性分类对应的销售属性
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);
    //添加三级分类对应的平台销售信息
    void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo);

    List<PmsBaseAttrInfo> getAttrInfoListByValueIds(Set valueIdSet);
}
