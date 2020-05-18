package com.atguigu.gmall.gmallmanageservice;


import com.atguigu.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class GmallManageServiceApplicationTests {
    @Autowired
    RedisUtil redisUtil;
    @Test
    public void contextLoads() {
        Jedis jedis = redisUtil.getJedis();
        String ok = jedis.set("string1","2","nx");
        System.out.println(ok);


    }

}
