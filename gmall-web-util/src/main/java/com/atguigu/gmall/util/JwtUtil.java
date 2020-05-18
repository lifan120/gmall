package com.atguigu.gmall.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.SignatureException;
import java.util.Map;

/**
 * @param
 * @return
 */
public class JwtUtil {



    /***
     * jwt加密
     * @param key
     * @param map
     * @param salt
     * @return
     */
    public static String encode(String key,Map map,String salt){

        // 加密算法
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder.addClaims(map);

        String token = jwtBuilder.compact();
        return token;
    }

    /***
     * jwt解密
     * @param key
     * @param token
     * @param salt
     * @return
     * @throws SignatureException
     */
    public static  Map decode(String key,String token,String salt)throws SignatureException {
        // 加密算法
        if(salt!=null){
            key+=salt;
        }
        Claims map = null;

        map = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

        return map;

    }

}
