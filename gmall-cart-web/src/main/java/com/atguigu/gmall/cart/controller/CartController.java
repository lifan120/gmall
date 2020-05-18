package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CartController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-15
 * @Description:
 */
@Controller
public class CartController {
    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;
    //添加购物车项
    @LoginRequired(isNeedToken = false)
    @RequestMapping(value = "addToCart",method = RequestMethod.POST)
    public String addToCart(OmsCartItem omsCartItem, HttpServletRequest request, HttpServletResponse response, Map map){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        PmsSkuInfo pmsSkuInfo = skuService.getSkuById(omsCartItem.getProductSkuId());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductSkuId(pmsSkuInfo.getId());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setIsChecked("1");
        omsCartItem.setProductCategoryId(omsCartItem.getProductCategoryId());
        //判断用户是否登录
        String memberId = (String) request.getAttribute("userId");//模拟用户id
        if(StringUtils.isBlank(memberId)){//未登录
            Cookie[] cookies = request.getCookies();
            //判断cookies中是否有购物车项
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)) {//有
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                //判断是否存在商品项
                boolean isExist = itemIsExist(omsCartItem,omsCartItems);
                if(isExist) {//修改
                    for (OmsCartItem omsCartItemForCookie : omsCartItems) {
                        if(omsCartItemForCookie.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            omsCartItemForCookie.setQuantity(omsCartItemForCookie.getQuantity().add(omsCartItem.getQuantity()));
                        }
                    }
                }else{
                    omsCartItems.add(omsCartItem);
                }
            }else{//如果cookies中没有数据那就直接添加
                omsCartItems.add(omsCartItem);
            }
            CookieUtil.setCookie(request,response,"cartListCookie", JSON.toJSONString(omsCartItems),1000*60*60*24,true);
        }else{//已经登录
            //判断db是否已经存在该sku项
            omsCartItem.setMemberId(memberId);
            OmsCartItem omsCartItemForDb = cartService.isCartExist(omsCartItem);
            if(omsCartItemForDb!=null){//如果不为空，则修改
                omsCartItemForDb.setQuantity(omsCartItemForDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemForDb);
            }else{//如果为空，则添加
                cartService.addCart(omsCartItem);
            }
        }
        map.put("skuInfo",pmsSkuInfo);
        return "redirect:/success";
    }
    //获得用户所有购物车项
    @RequestMapping("/cartList")
    public String cartList(HttpServletResponse response,HttpServletRequest request,Map map){
        String memberId = (String) request.getAttribute("userId");
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //判断用户是否登录
        if(StringUtils.isNotBlank(memberId)){//已登录
            omsCartItems = cartService.getCartList(memberId);//从数据库中查询
        }else{
            //未登录就从cookie中获取
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
        }
        map.put("cartList",omsCartItems);
        map.put("sumPrice",getSumPrice(omsCartItems));
        return "cartList";
    }

    //更新购物车选中状态
    @RequestMapping("/checkCart")
    public String checkCart(OmsCartItem omsCartItem,HttpServletRequest request,HttpServletResponse response,Map map){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");
        //判断是否登录，登录从db取，未登录从cookie中取值
        if(StringUtils.isNotBlank(userId)){//已登录
            omsCartItem.setMemberId(userId);
            cartService.updateCart(omsCartItem);
            omsCartItems = cartService.getCartList(userId);
        }else{//未登录
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if(StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie, OmsCartItem.class);
                for(OmsCartItem omsCartItemForCookie : omsCartItems){
                    if(omsCartItemForCookie.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                        omsCartItemForCookie.setIsChecked(omsCartItem.getIsChecked());
                    }
                }
                CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(omsCartItems),1000*60*60*24,true);
            }
        }
        map.put("cartList",omsCartItems);
        map.put("sumPrice",getSumPrice(omsCartItems));
        //返回一个内嵌页面
        return "cartListinner";
    }


    private boolean itemIsExist(OmsCartItem omsCartItem, List<OmsCartItem> cartList) {
        boolean isExist = false;
        for(OmsCartItem omsCartItemForCookie : cartList){
            if(omsCartItem.getProductSkuId().equals(omsCartItemForCookie.getProductSkuId())){
                isExist = true;
                break;
            }
        }



        return isExist;
    }
    private BigDecimal getSumPrice(List<OmsCartItem> omsCartItems){
        BigDecimal sumPrice = new BigDecimal("0");
        for(OmsCartItem omsCartItem : omsCartItems){
            if(omsCartItem.getIsChecked().equals("1")){
                sumPrice=sumPrice.add(omsCartItem.getQuantity().multiply(omsCartItem.getPrice()));
            }
        }

        return sumPrice;
    }

    @RequestMapping("success")
    public String success(){
        return "success";
    }
}
