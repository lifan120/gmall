package com.atguigu.gmall.serach;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallSerachServiceApplicationTests {
    @Autowired
    JestClient jestClient;
    @Reference
    SkuService skuService;
    //将所有sku信息通过es api导入es
    @Test
    public void contextLoads() throws IOException {
        List<PmsSkuInfo> skuInfoList = skuService.getAllskuInfo();
        //将skuInfo映射到es的数据结构中serachSkuInfo
        List<PmsSearchSkuInfo> searchSkuInfos = new ArrayList<>();
        PmsSearchSkuInfo pmsSearchSkuInfo = null;
        for(PmsSkuInfo pmsSkuInfo : skuInfoList){
            pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            searchSkuInfos.add(pmsSearchSkuInfo);
        }

        //添加到es中
        for(PmsSearchSkuInfo searchSkuInfo : searchSkuInfos) {
            Index index = new Index.Builder(searchSkuInfo).index("gmall").type("pmsSearchSkuInfo").id(searchSkuInfo.getId()+"").build();
            jestClient.execute(index);
        }

    }

}
