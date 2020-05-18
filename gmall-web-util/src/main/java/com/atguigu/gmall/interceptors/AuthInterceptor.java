package com.atguigu.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.annotations.LoginRequired;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpclientUtil;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthInterceptor
 *
 * @Author: 李繁
 * @CreateTime: 2020-03-18
 * @Description:
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    //在请求到达之前拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{

        //在拦截之前先判断用户请求的业务是否需要验证token
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        if(methodAnnotation == null){
            return true;
        }

        String newToken = request.getParameter("newToken");//一次都没有验证的token
        String oldToken = CookieUtil.getCookieValue(request,"oldToken",true);
        String token = "";
        if(StringUtils.isNotBlank(oldToken)){
            token = oldToken;
        }
        if(StringUtils.isNotBlank(newToken)){
            token = newToken;
        }
        if(StringUtils.isNotBlank(token)) {//如果token不为空
            //调用验证中心的接口进行验证
           /* String ip = request.getRemoteAddr();//如果是负载均衡的话就是负载均衡的ip
            System.out.println(ip+"_token不为空");
            String clientIp = request.getHeader("X-forwarded-for");//如果是负载均衡的话就是客户端的ip
            String result = HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?" + "ip=" + ip + "&" + "token=" + token);
            Map map = new HashMap();
            map = JSON.parseObject(result, map.getClass());*/

            /*通过jwt进行本地化验证*/
            // jwt算法解析代替远程访问cas
            // 服务器密钥
            String serverKey = "comatguigugmall";
            // 盐值
            String salt = request.getRemoteAddr();// 可以加入自定义的加密处理算法
            // jwt去中心化，本地verify
            Map map = JwtUtil.decode(serverKey, token, salt);

            if(map!=null){//成功
                //写入cookie
                request.setAttribute("userId",map.get("userId"));
                request.setAttribute("nickName",map.get("nickName"));
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*24,true);
                return true;
            }

        }
        boolean isNeedToken = methodAnnotation.isNeedToken();
        if(isNeedToken){//验证成功token通过才能访问
            String returnUrl = request.getRequestURL().toString();
            response.sendRedirect("http://passport.gmall.com:8085/index?returnUrl="+returnUrl);
            return false;
        }
        return true;
    }
}
