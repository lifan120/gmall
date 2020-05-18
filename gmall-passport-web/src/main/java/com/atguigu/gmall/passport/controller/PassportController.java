package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PassportController
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-18
 * @Description:
 */
@Controller
public class PassportController {
    @Reference
    UserService userService;

    @Reference
    SkuService skuService;

    @RequestMapping("/index")
    public String index(String returnUrl, Map map){
        map.put("returnUrl",returnUrl);
        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UmsMember umsMember){
        //通过Userservice比对
        UmsMember umsMemberForDb = userService.checkUmsMember(umsMember);
        //颁发token
        // 生成token
//        String ip = request.getRemoteAddr();//直接获得请求ip,负载均衡ip
//        String headerIp = request.getHeader("X-forwarded-for");//客户端请求ip

        if(umsMemberForDb != null) {
            // 服务器密钥
            String serverKey = "comatguigugmall";

            // 盐值
            String ip = request.getRemoteAddr();// 可以加入自定义的加密处理算法,如果负载均衡则是负载均衡的ip
            System.out.println("login的请求ip:" + ip);
            // 用户
            Map<String, String> map = new HashMap<>();
            map.put("userId", umsMemberForDb.getId());
            map.put("nickName", umsMemberForDb.getNickname());
            // jwt算法加密生成token
            String token = JwtUtil.encode(serverKey, map, ip);
            return token;
        }
        return "fail";
    }
    //验证方法，返回的应该是验证是否成功等，还要给调用者返回一些用户信息
    @RequestMapping("verify")
    @ResponseBody
    public Map verify(String ip,String token){
        //如果使用cas认证中心的方式校验，那么则在登录时使用算法生成token，然后将token设计为redis的key到redis
        //校验的时候通过设计的key取出user，看是否为空，如果为空则校验失败
        Map map = new HashMap();
        //验证
        if(StringUtils.isNotBlank(token)) {//此处应该加验证算法
            map.put("status", "success");
            map.put("userId","1");
            map.put("nickName","tomIsNumberOne");
        }else{
            map.put("status","fail");
        }
        return map;
    }

    @RequestMapping("test")
    @ResponseBody
    public String test(){
        List<PmsSkuInfo> allskuInfo = skuService.getAllskuInfo();
        System.out.println(allskuInfo);

        return "hello";
    }

    public static void main(String[] args) throws SignatureException {
        Map map = new HashMap();
        map.put("a","b");
        String encode = JwtUtil.encode("abc", map, "aa");
        System.out.println(encode);
        Map decode = JwtUtil.decode("abc", encode, "aasss");
        System.out.println(decode);
    }

}
