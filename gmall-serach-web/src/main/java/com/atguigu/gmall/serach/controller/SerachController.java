package com.atguigu.gmall.serach.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.SearchService;
import com.atguigu.gmall.service.SkuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * SerachController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-13
 * @Description:
 */
@Controller
public class SerachController {
    @Reference
    SearchService searchService;
    @Reference
    AttrService attrService;

    @RequestMapping("/list.html")
    public String list(PmsSearchParam pmsSearchParam, Map map){
        //将搜索到的sku数据放到域中
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.search(pmsSearchParam);
        map.put("skuLsInfoList",pmsSearchSkuInfos);
        //然后需要将检索出来的数据的valueId去掉重复的（平台销售属性值Id）
        Set valueIdSet = new HashSet();
        List<PmsSkuAttrValue> skuAttrValueList = null;
        for(PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos){
           skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
           for(PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList){
               String valueId = pmsSkuAttrValue.getValueId();
               valueIdSet.add(valueId);
           }
        }
        //使用去重后的平台销售属性值Id(valueId)查询出检索出的sku数据的所有平台属性信息放到页面
        if(valueIdSet.size()!=0){
            List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrInfoListByValueIds(valueIdSet);
            String valueIds[] = pmsSearchParam.getValueId();
            //面包屑
            List<PmsSearchCrumb> attrValueSelectedList = new ArrayList<>();
            PmsSearchCrumb pmsSearchCrumb = null;
            if(pmsBaseAttrInfos != null && valueIds!=null && valueIds.length > 0) {
                //去掉已经选择的属性信息
                Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfos.iterator();
                PmsBaseAttrInfo pmsBaseAttrInfo = null;
                List<PmsBaseAttrValue> pmsBaseAttrValues = null;
                //用于判断是否已经删除元素,并且将面包屑添加到域中
                while(iterator.hasNext()){
                    boolean isRemove = false;
                    pmsBaseAttrInfo = iterator.next();
                    pmsBaseAttrValues  = pmsBaseAttrInfo.getAttrValueList();
                    for(PmsBaseAttrValue pmsBaseAttrValue : pmsBaseAttrValues){
                        for(String valueId : valueIds){
                            //如果该条属性已经被选择则删除
                            if(valueId.equals(pmsBaseAttrValue.getId())){
                                pmsSearchCrumb = new PmsSearchCrumb();
                                pmsSearchCrumb.setValueId(valueId);
                                pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());//valueName
                                String crumbUrlParam = getUrlParamForCrumb(pmsSearchParam, valueId);
                                pmsSearchCrumb.setUrlParam(crumbUrlParam);
                                attrValueSelectedList.add(pmsSearchCrumb);
                                iterator.remove();
                            }
                        }
                    }
                }
                map.put("attrValueSelectedList",attrValueSelectedList);
            }

           /* //面包屑功能
            if(valueIds!=null && valueIds.length>0) {
                List<PmsSearchCrumb> attrValueSelectedList = new ArrayList<>();
                PmsSearchCrumb pmsSearchCrumb = null;
                for (String valueId : valueIds) {
                    //设置面包屑的valueId与valueName
                    pmsSearchCrumb = new PmsSearchCrumb();
                    pmsSearchCrumb.setValueId(valueId);
                    pmsSearchCrumb.setValueName(valueId);//valueName
                    //根据当前请求的url-面包屑的值id可以得出面包屑的url；
                    String crumbUrlParam = getUrlParamForCrumb(pmsSearchParam, valueId);
                    pmsSearchCrumb.setUrlParam(crumbUrlParam);
                    attrValueSelectedList.add(pmsSearchCrumb);
                }
                map.put("attrValueSelectedList",attrValueSelectedList);
            }*/

            map.put("attrList",pmsBaseAttrInfos);
        }
        //获取当前页面url
        String urlParam = getCurrentUrlParam(pmsSearchParam);
        map.put("urlParam",urlParam);
        //面包屑功能,面包屑可以根据已经选择的属性值id得出,面包屑的url就是当前url-面包屑的valueId
        String valueIds[] = pmsSearchParam.getValueId();

        return "list";
    }
    //根据当前请求的url-面包屑的值id可以得出面包屑的url；
    private String getUrlParamForCrumb(PmsSearchParam pmsSearchParam, String valueIdForCrumb) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String valueIds[] = pmsSearchParam.getValueId();
        String urlParam = "";

        //如果catalog3Id不为空
        if(StringUtils.isNotBlank(catalog3Id)){
            urlParam = urlParam + "catalog3Id="+ catalog3Id;
        }

        //如果Keyword不为空
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam +"&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(valueIds!=null&&valueIds.length!=0){
            for(String valueId : valueIds){
                if(valueIdForCrumb == valueId) continue;
                urlParam = urlParam + "&valueId="+valueId;
            }
        }
        return urlParam;
    }

    //根据请求参数获取当前页面的urlParam,拼接起来
    private String getCurrentUrlParam(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String valueIds[] = pmsSearchParam.getValueId();
        String urlParam = "";

        //如果catalog3Id不为空
        if(StringUtils.isNotBlank(catalog3Id)){
            urlParam = urlParam + "catalog3Id="+ catalog3Id;
        }

        //如果Keyword不为空
        if(StringUtils.isNotBlank(keyword)){
            if(StringUtils.isNotBlank(urlParam)){
                urlParam = urlParam +"&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if(valueIds!=null&&valueIds.length!=0){
            for(String valueId : valueIds){
                urlParam = urlParam + "&valueId="+valueId;
            }
        }



        return urlParam;
    }


    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
