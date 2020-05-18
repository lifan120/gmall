package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.util.GmallUploadUtil;
import com.atguigu.gmall.service.SpuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * SpuController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-04
 * @Description:
 */
//spu信息管理
@RestController
@CrossOrigin
public class SpuController {
    //根据三级分类id查询该分类下所有的spu
    @Reference
    SpuService spuService;
    @RequestMapping("/spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {
        return spuService.getSpuList(catalog3Id);
    }

    //获取基础销售属性
    @RequestMapping("/baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList(){
        return spuService.getBaseSaleAttrList();
    }

    //上传图片
    @RequestMapping("/fileUpload")
    public String fileUpload(MultipartFile file){
        String url = GmallUploadUtil.upLoadImg(file);
        //此处还需向redis添加图片名称，随后进行quratz定时删除
        return url;
    }

    //添加spu信息
    @RequestMapping("/saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){
        spuService.saveSpuInfo(pmsProductInfo);
        return "success";
    }
    //获取所有spu销售属性
    @RequestMapping("/spuSaleAttrList")
    public List<PmsProductSaleAttr> getPmsProductSaleAttrList(String spuId){
          List<PmsProductSaleAttr> spuList = spuService.getPmsProductSaleAttrList(spuId);
          return spuList;
    }
    //获取spu所有图片
    @RequestMapping("/spuImageList")
    public List<PmsProductImage> getSpuImgList(String spuId){
         List<PmsProductImage> spuImageList = spuService.getImgList(spuId);
        return spuImageList;
    }
}
