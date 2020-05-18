package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsBaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * AttrServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-04
 * @Description:
 */
@Service
public class AttrServiceImpl implements AttrService{
    @Autowired
    PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    //获取所有三级分类对应的平台销售属性
    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> attrInfoList = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        //通过平台销售属性查询销售属性值
        PmsBaseAttrValue pmsBaseAttrValue = null;
        for(PmsBaseAttrInfo attrInfo : attrInfoList){
            pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(attrInfo.getId());
            List<PmsBaseAttrValue> attrValueList = pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            attrInfo.setAttrValueList(attrValueList);
        }
        return attrInfoList;
    }
    //添加三级分类对应的平台销售信息
    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {
        pmsBaseAttrInfoMapper.insert(pmsBaseAttrInfo);
        String attrId = pmsBaseAttrInfo.getId();
        //将值循环插入销售属性值表
        List<PmsBaseAttrValue> attrValues = pmsBaseAttrInfo.getAttrValueList();
        for(PmsBaseAttrValue pmsBaseAttrValue : attrValues){
            pmsBaseAttrValue.setAttrId(attrId);
            pmsBaseAttrValueMapper.insert(pmsBaseAttrValue);
        }
    }
    //根据一些属性值id查询平台属性信息
    @Override
    public List<PmsBaseAttrInfo> getAttrInfoListByValueIds(Set valueIdSet) {
        String ValueIds = StringUtils.join(valueIdSet, ",");
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrInfoMapper.selectPmsBaseAttrInfoByValueIds(ValueIds);
        return pmsBaseAttrInfos;
    }
}
