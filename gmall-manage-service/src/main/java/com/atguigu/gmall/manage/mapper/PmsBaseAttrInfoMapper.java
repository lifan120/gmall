package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * PmsBaseAttrInfoMapper
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-04
 * @Description:
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo>{
    //根据平台属性值id查询对应的平台属性
    List<PmsBaseAttrInfo> selectPmsBaseAttrInfoByValueIds(@Param("valueIds") String valueIds);
}
