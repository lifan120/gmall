package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsSkuAttrValue;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.PmsSkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuImageMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuInfoMapper;
import com.atguigu.gmall.manage.mapper.PmsSkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * SkuServiceImpl
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-07
 * @Description:
 */
@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    RedisUtil redisUtil;
    //保存sku商品信息
    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        pmsSkuInfoMapper.insert(pmsSkuInfo);
        //保存sku的图片信息
        String skuId = pmsSkuInfo.getId();
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for(PmsSkuImage pmsSkuImage : skuImageList){
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insert(pmsSkuImage);
        }
        //保存sku与平台销售属性关联的信息,也就是通过该信息查询sku的平台销售属性与值
        List<PmsSkuAttrValue> attrValues = pmsSkuInfo.getSkuAttrValueList();
        for(PmsSkuAttrValue pmsSkuAttrValue : attrValues){
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
        }
        //保存sku与spu销售属性的关系
        List<PmsSkuSaleAttrValue> saleAttrValues = pmsSkuInfo.getSkuSaleAttrValueList();
        for(PmsSkuSaleAttrValue pmsSkuSaleAttrValue : saleAttrValues){
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
        }
    }
    //根据spuID获取当前spu下所有sku
    @Override
    public List<PmsSkuInfo> getAllSkuInfoBySpuId(String spuId) {
        List<PmsSkuInfo> skuInfoList = pmsSkuInfoMapper.selectAllSkuInfoBySpuId(spuId);
        return skuInfoList;
    }



    //通过skuId获取sku信息，暂时只需要获取sku的图片信息与sku信息
    //为了降低db压力，从数据库查询出来的数据先放到redis中去以{sku:skuID:info,skuinfoJson的方式}
    @Override
    public PmsSkuInfo getSkuById(String skuId) {
        //首先判断Redis中存不存在数据，如果存在则从Redis中取，如果不存在则从数据库取然后再转化成json存到redis
        Jedis jedis = null;
        PmsSkuInfo SkuInfo = null;
        try {
            jedis = redisUtil.getJedis();
            //问题：如果利用不存在的skuID会造成缓存穿透：解决方案，将从数据库查询出Null的值也存到redis，设置较短的过期时间
            //问题：如果都设置一样的时间，在同一时间所有缓存都过期（缓存雪崩），所有请求都到数据库压力过大导致数据库被压垮//将各个缓存过期时间设置为随机时间
            //问题：如果某一个比较热门的缓存在某一时间过期，这时候高并发热门缓存的话这样就会造成缓存击穿，解决方案:分布式锁

            String skuJson = jedis.get("sku:" + skuId + ":info");
            if (StringUtils.isBlank(skuJson)) {//缓存中没有数据
                String uuid = UUID.randomUUID().toString();
                String ok = jedis.set("sku:"+skuId+"lock",uuid,"nx","px",10000);
                if(!StringUtils.isBlank(ok)&&ok.equals("OK")){
                SkuInfo = getSkuByIdFromDb(skuId);
                if(SkuInfo != null){//将各个缓存过期时间设置为随机时间
                    //将数据库查询出来的skuInfo放到redis
                    jedis.set("sku:"+skuId+":info", JSON.toJSONString(SkuInfo));
                    jedis.expire("key", 300000);
                }else{ //将从数据库查询出Null的值也存到redis，设置较短的过期时间
                    jedis.set("sku:"+skuId+":info", JSON.toJSONString(SkuInfo));
                    jedis.expire("key", 300000);
                }
                }else{//没拿到锁就等三秒后再进入该方法 自旋
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return getSkuById(skuId);
                }
                //防止刚好在锁过期的时候可能会删除刚好进来的别的线程设置的锁，写一个lua脚本，该脚本可以将锁的uuid和本地uuid进行比对，如果不一样则不删除，一样则删除，可以解决这个问题
                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList("sku:" + skuId + ":lock"),Collections.singletonList(uuid));
            }else{//不为空则直接从redis中取
                skuJson  = jedis.get("sku:" + skuId + ":info");
                SkuInfo = JSON.parseObject(skuJson,PmsSkuInfo.class);
            }

        }finally {
            jedis.close();
        }
        return SkuInfo;
    }

    //将所有sku信息查询出来
    @Override
    public List<PmsSkuInfo> getAllskuInfo() {
        List<PmsSkuInfo> skuInfoList = pmsSkuInfoMapper.selectAll();
        for(PmsSkuInfo pmsSkuInfo : skuInfoList){
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> skuAttrValueList = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(skuAttrValueList);
        }

        return skuInfoList;
    }

    //通过skuId获取sku信息，暂时只需要获取sku的图片信息与sku信息
    private PmsSkuInfo getSkuByIdFromDb(String skuId) {
        PmsSkuInfo skuInfo = new PmsSkuInfo();
        skuInfo.setId(skuId);
        PmsSkuInfo skuInfoResult = pmsSkuInfoMapper.selectOne(skuInfo);
        PmsSkuImage skuImage = new PmsSkuImage();
        skuImage.setSkuId(skuId);
        List<PmsSkuImage> skuImageList = pmsSkuImageMapper.select(skuImage);
        skuInfoResult.setSkuImageList(skuImageList);
        return skuInfoResult;
    }
}
