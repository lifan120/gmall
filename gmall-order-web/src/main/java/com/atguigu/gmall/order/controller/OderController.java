package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * OderController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-19
 * @Description:
 */
@Controller
public class OderController {
    @Reference
    CartService cartService;
    @Reference
    UserService userService;
    @Reference
    OrderService orderService;
    @LoginRequired(isNeedToken = true)
    @RequestMapping("/toTrade")
    public String toTrade(HttpServletRequest request, Map map){
        //通过拦截器传过来的userId将购物车中的购物项放到订单结算页面当中 nickName
        String userId = (String)request.getAttribute("userId");
        String nickName = (String)request.getAttribute("nickName");
        //通过userId获取所有的购物车商品，将已经勾选的添加到orderitem中
        List<OmsCartItem> cartList = cartService.getCartList(userId);
        if(cartList != null && cartList.size()>0){
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for(OmsCartItem omsCartItem : cartList){
                if(omsCartItem.getIsChecked().equals("1")){
                    OmsOrderItem omsOrderItem = new OmsOrderItem();
                    omsOrderItem.setProductId(omsCartItem.getProductId());
                    omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                    omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                    omsOrderItem.setProductName(omsCartItem.getProductName());
                    omsOrderItem.setProductPic(omsCartItem.getProductPic());
                    omsOrderItem.setProductPrice(omsCartItem.getPrice());
                    omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                    omsOrderItems.add(omsOrderItem);
                }
            }
            //获取用户所有收货地址
            List<UmsMemberReceiveAddress> umsMemberReceiveAddressByUserId = userService.getUmsMemberReceiveAddressByUserId(userId);
            //生成一个交易码防止用户重复提交订单
            String tradeCode = orderService.createTradeCode(userId);
            map.put("tradeCode",tradeCode);
            map.put("userAddressList",umsMemberReceiveAddressByUserId);
            map.put("orderDetailList",omsOrderItems);
            map.put("nickName",nickName);
            map.put("totalAmount",getSumPrice(cartList));
        }

        return "trade";
    }
    @LoginRequired(isNeedToken = true)
    @RequestMapping("/submitOrder")
    public String submitOrder(HttpServletRequest request, HttpServletResponse response,String tradeCode,String addressId){
        String  userId = (String) request.getAttribute("userId");
        String  nickName = (String) request.getAttribute("nickName");
        //检查tradeCode是否有效
        boolean b = orderService.checkTradeCode(userId,tradeCode);
        if(b){ //生成订单
            //首先获取到购物车中选中的数据
            List<OmsCartItem> cartListByUser = cartService.getCartList(userId);
            //获取用户选择的收货地址
            UmsMemberReceiveAddress umsMemberReceiveAddress = userService.getUmsMemberReceiveAddressByAddressId(addressId);
            //每生成一条订单详情就需要删除一条购物车数据
            List<String> delCartIds = new ArrayList<>();
            OmsOrder omsOrder = new OmsOrder();
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            //外部表示订单号 一般为公司名+时间字符串+时间戳
            String out_trade_no = getOrderSn();
            omsOrder.setOrderSn(out_trade_no);
            omsOrder.setStatus("0");//订单付款状态
            omsOrder.setTotalAmount(getSumPrice(cartListByUser));//总价钱
            omsOrder.setMemberId(userId);
            omsOrder.setMemberUsername(nickName);
            omsOrder.setPayAmount(getSumPrice(cartListByUser));
            omsOrder.setCreateTime(new Date());
            omsOrder.setSourceType(1);//订单来源 pc或app
            omsOrder.setPayType(0);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,3);
            Date calaendarDate = calendar.getTime();
            omsOrder.setReceiveTime(calaendarDate);
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            for(OmsCartItem omsCartItem : cartListByUser){
                OmsOrderItem omsOrderItem = new OmsOrderItem();
                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductPrice(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setOrderSn(out_trade_no);
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItems.add(omsOrderItem);
                delCartIds.add(omsCartItem.getId());
            }
            omsOrder.setOmsOrderItems(omsOrderItems);
            //保存订单信息，重定向到支付模块页面
            orderService.saveOrder(omsOrder);
            //删除购物车中数据
            cartService.deleteCartList(delCartIds,userId);
           /* 重定向到支付系统*/
            return "redirect:http://pay.gmall.com:8088/index?out_trade_no="+out_trade_no;
        }
        return "redirect:tradeFail";
    }

    private String getOrderSn() {
        String atguigu = "atguigu";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddmmss");
        String time = dateFormat.format(date);
        long sysTime = System.currentTimeMillis();
        String sn = atguigu+time+sysTime;
        return sn;
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
}
