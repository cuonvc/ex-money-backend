package com.exmoney.security.jwt;

import com.exmoney.entity.User;
import com.exmoney.exception.APIException;
import com.exmoney.exception.ResourceNotFoundException;
import com.exmoney.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY = "SnNvbiB3ZWIgdG9rZW4gZm9yIG1pY3Jvc2VydmljZSBwcm9qZWN0";
    private static final Long expireTime = 7776000000L; //3-months

    private final UserRepository userRepository;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    //validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
        } catch (MalformedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
        }
    }

    public String generateToken(String email) {
//        String email = authentication.getName();
        Date currentDate = new Date();
        Date expire = new Date(currentDate.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claimsBuilder(email))
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expire)
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }
    private Map<String, Object> claimsBuilder(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

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
