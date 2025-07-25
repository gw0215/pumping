package com.pumping.global.common.util;

import com.pumping.domain.member.model.Member;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final String secretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateToken(Member member) {
        return Jwts.builder()
                .setSubject(String.valueOf(member.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long validateAndExtractMemberId(String token) {
        try {
            return Long.valueOf(Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
        }
        return null;
    }
}
