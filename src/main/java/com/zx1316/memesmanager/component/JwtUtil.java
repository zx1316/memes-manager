package com.zx1316.memesmanager.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {
    @Value("${my.secret}")
    private String secret;

    // 创建token
    public String createToken(UserDetails userDetails) {
        return Jwts.builder()
                .claims(new HashMap<>())
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 16)) // 16小时过期
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    // 验证token
    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return null;
            }
            return username;
        } catch (Exception e) {
            return null;
        }
    }
}
