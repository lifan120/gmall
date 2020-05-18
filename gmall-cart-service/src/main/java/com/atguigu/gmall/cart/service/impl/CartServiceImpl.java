package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.cart.mapper.OmsCartItemMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CartService
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-16
 * @Description:
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsCartItemMapper omsCartItemMapper;
    //根据会员id与skuId查询数据库是否有此项数据,如果有则返回
    @Override
    public OmsCartItem isCartExist(OmsCartItem omsCartItem) {
        OmsCartItem omsCartItemParam = new OmsCartItem();
        omsCartItemParam.setMemberId(omsCartItem.getMemberId());
        omsCartItemParam.setProductSkuId(omsCartItem.getProductSkuId());
        OmsCartItem omsCartItemResult = omsCartItemMapper.selectOne(omsCartItemParam);
        return omsCartItemResult;
    }
    //修改购物车中数据
    @Override
    public void updateCart(OmsCartItem omsCartItemForDb) {
        Example example  = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo(omsCartItemForDb.getProductSkuId()).andEqualTo(omsCartItemForDb.getMemberId());
        OmsCartItem omsCartItemForUpdate = new OmsCartItem();
        if(omsCartItemForDb.getQuantity()!=null){
            omsCartItemForUpdate.setQuantity(omsCartItemForDb.getQuantity());
            omsCartItemMapper.updateByExampleSelective(omsCartItemForUpdate,example);
        }
        if(StringUtils.isNotBlank(omsCartItemForDb.getIsChecked())){
            omsCartItemForUpdate.setIsChecked(omsCartItemForDb.getIsChecked());
            omsCartItemMapper.updateByExampleSelective(omsCartItemForUpdate,example);
        }
        //更新到缓存中
        Jedis jedis = redisUtil.getJedis();
        jedis.hset("member_"+omsCartItemForDb.getMemberId()+"_cart",omsCartItemForDb.getProductSkuId(), JSON.toJSONString(omsCartItemForDb));
        jedis.close();

    }
    //先从缓存中获取购物车的数据，如果没有则从db中获取购物车中的数据,使用redis的hash结构 设计好缓存的key为member_memberId_cart 使用skuid作为field
    @Override
    public List<OmsCartItem> getCartList(String memberId) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        Jedis jedis = redisUtil.getJedis();
        List<String> omsCartItemJsonList= jedis.hvals("user_" + memberId + "_cart");
        if(omsCartItemJsonList != null && omsCartItemJsonList.size()>0){//从缓存取
            for(String omsCartItemJson : omsCartItemJsonList){
                OmsCartItem omsCartItem = JSON.parseObject(omsCartItemJson, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }
        }else{//从db取
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(memberId);
            omsCartItems = omsCartItemMapper.select(omsCartItem);
            if(omsCartItems != null && omsCartItems.size() > 0){//同步到缓存
                Map map = new HashMap();
                for(OmsCartItem omsCartItemForDb : omsCartItems){
                    map.put(omsCartItemForDb.getProductSkuId(),JSON.toJSONString(omsCartItemForDb));
                }
                jedis.hmset("member_"+memberId+"_cart",map);
            }
        }
        jedis.close();
        return omsCartItems;
    }

    @Override
    public void deleteCartList(List<String> delCartList,String userId) {
        String ids = StringUtils.join(delCartList, ",");
        omsCartItemMapper.deleteCarts(ids);
        //此处需要把缓存中的购物车数据也刷新一下

    }

    //添加商品进入购物车
    @Override
    public void addCart(OmsCartItem omsCartItem) {
        omsCartItemMapper.insertSelective(omsCartItem);
        //添加到缓存中
        Jedis jedis = redisUtil.getJedis();
        jedis.hset("member_"+omsCartItem.getMemberId()+"_cart",omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));
        jedis.close();
    }



}
