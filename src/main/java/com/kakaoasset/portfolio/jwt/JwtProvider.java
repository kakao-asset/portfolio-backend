package com.kakaoasset.portfolio.jwt;

import com.kakaoasset.portfolio.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.password}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    /* 토큰 생성 메소드 */
    public String createAccessToken(String id) {
        Claims claims = Jwts.claims().setSubject(id);
        claims.put("role", "ROLE_USER");
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofMinutes(30).toMillis()); // 임시만료기간: 30분

        return Jwts.builder()
                .setIssuer(issuer) // 토큰발급자(iss)
                .setClaims(claims)
                .setIssuedAt(now) // 발급시간(iat)
                .setExpiration(expiration) // 만료시간(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken() {
        Claims claims = Jwts.claims();
        claims.put("value", UUID.randomUUID());

        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(14).toMillis()); // 임시만료기간: 14일

        return Jwts.builder()
                .setIssuer(issuer) // 토큰발급자(iss)
                .setClaims(claims)
                .setIssuedAt(now) // 발급시간(iat)
                .setExpiration(expiration) // 만료시간(exp)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Long getExpirationTime(String token) {
        Date expiration = Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token).getBody().getExpiration();

        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public boolean isTokenValid(String token) {
        return getTokenClaims(token) != null;
    }

    public Claims getTokenClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody(); // token의 Body가 하기 exception들로 인해 유효하지 않으면 각각에 해당하는 로그 콘솔에 찍음
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            // 처음 로그인(/auth/kakao, /auth/google) 시, AccessToken(AppToken) 없이 접근해도 token validate을 체크하기 때문에 exception 터트리지 않고 catch합니다.
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Authentication getAuthentication(String token) {

        if(isTokenValid(token)) {

            Claims claims = getTokenClaims(token);
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get("role").toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);

            // 사실상 principal에 저장되는 값은 socialId값과 role뿐(소셜 로그인만 사용하여 password 저장하지 않아 ""로 넣음)
            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } else {
            throw new UnauthorizedException();
        }
    }
}
