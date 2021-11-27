package com.teenteen.topping.utils;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class JwtService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    public String createJwt(Long userId) {
        Date now = new Date();
        Long expiredTime = 7 * 1000 * 60L * 60L * 24L; // 유효기간 24시간 (테스트용으로 우선 7일)

        Date nowExpiredTime = new Date();       // 유효기간 끝나는 시각
        nowExpiredTime.setTime(nowExpiredTime.getTime() + expiredTime);

        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(nowExpiredTime)
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }
    public String createRefreshToken(Long userId) {
        Date reNow = new Date();
        Long expiredTime = 1000 * 60L * 60L * 24L * 60L; // 유효기간 60일

        Date nowExpiredTime = new Date();       // 유효기간 끝나는 시각
        nowExpiredTime.setTime(nowExpiredTime.getTime() + expiredTime);

        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(reNow)
                .setExpiration(nowExpiredTime)
                .signWith(SignatureAlgorithm.HS256, Secret.REFRESH_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes())
                        .getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /*
   JWT 검증(refreshToken)
    */
    public Boolean verifyRefreshJWT(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Secret.REFRESH_SECRET_KEY)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {   // 토큰 만료
            System.out.println(e);
            return false;
        } catch (Exception e) {     // 그 외 에러
            System.out.println(e);
            return false;
        }
        return true;
    } /*
   JWT 검증(refreshToken)
    */
    public Boolean verifyJWT(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {   // 토큰 만료
            System.out.println(e);
            return false;
        } catch (Exception e) {     // 그 외 에러
            System.out.println(e);
            return false;
        }
        return true;
    }

    /*
    JWT에서 userId 추출
    @return Long
     */

    public Long getUserId() throws BaseException {
        String accessToken = getJwt();
        if (accessToken.equals("") || accessToken.length() == 0) {
            throw new BaseException(BaseResponseStatus.EMPTY_JWT);
        }
        if(!verifyJWT(accessToken))
            throw new BaseException(BaseResponseStatus.INVALID_JWT);
        // parsing
        Jws<Claims> claims;
        claims = Jwts.parser()
                .setSigningKey(Secret.JWT_SECRET_KEY)
                .parseClaimsJws(accessToken);

        return claims.getBody().get("userId", Long.class);
    }

    public Long getUserIdFromRefreshToken(String accessToken) {
        Jws<Claims> claims;

        claims = Jwts.parser()
                .setSigningKey(Secret.REFRESH_SECRET_KEY)
                .parseClaimsJws(accessToken);

        return claims.getBody().get("userId",Long.class);
    }
}
