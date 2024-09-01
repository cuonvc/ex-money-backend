package com.exmoney.security.jwt;

import com.exmoney.entity.User;
import com.exmoney.payload.dto.AccessTokenDto;
import com.exmoney.service.CommonService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.exmoney.payload.enumerate.ErrorCode.*;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "SnNvbiB3ZWIgdG9rZW4gZm9yIG1pY3Jvc2VydmljZSBwcm9qZWN0";
    private static final Long expireTime = 7776000000L; //3-months

    private final CommonService commonService;

    public JwtTokenProvider(CommonService commonService) {
        this.commonService = commonService;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //validate JWT token
    //Check nếu 401 thì request renew access token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            commonService.throwException(JWT_INVALID_SIGNATURE, Locale.forLanguageTag("us"));
        } catch (MalformedJwtException e) {
            commonService.throwException(JWT_INVALID_TOKEN, Locale.forLanguageTag("us"));
        } catch (ExpiredJwtException e) {
            commonService.throwException(JWT_EXPIRED_TOKEN, Locale.forLanguageTag("us"));
        } catch (UnsupportedJwtException e) {
            commonService.throwException(JWT_UNSUPPORTED_TOKEN, Locale.forLanguageTag("us"));
        } catch (IllegalArgumentException e) {
            commonService.throwException(JWT_CLAIM_IS_EMPTY, Locale.forLanguageTag("us"));
        }
        return false;
    }

    public AccessTokenDto generateToken(String email) {
//        String email = authentication.getName();
        Date currentDate = new Date();
        Date expire = new Date(currentDate.getTime() + expireTime);

        String accessToken = Jwts.builder()
                .setClaims(claimsBuilder(email))
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expire)
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

        return AccessTokenDto.builder()
                .token(accessToken)
                .tokenType("Bearer")
                .expireDate(expire)
                .build();
    }
    private Map<String, Object> claimsBuilder(String email) {
        User user = commonService.findUserByEmailOrThrow(email, Locale.getDefault());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole());

        return claims;
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
