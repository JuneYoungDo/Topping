package com.teenteen.topping.user;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.oauth.OauthService.AppleService2;
import com.teenteen.topping.oauth.OauthService.KakaoService;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.Bcrypt;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.teenteen.topping.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Bcrypt bcrypt;
    private final KakaoService kakaoService;
    private final AppleService2 appleService2;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public void createUser(String email) {
        User user = User.builder()
                .email(email)
                .birth(null)
                .nickname(null)
                .level(0)
                .refreshToken("")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        save(user);
    }

    public boolean isUserNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElse(null);
        if (user != null && user.isDeleted() == false)
            return true;
        else
            return false;
    }

    public String isUsedEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "free";
        else if (user.isDeleted() == true) return "deleted";
        else return "using";
    }

    @Transactional
    public LoginRes socialLogin(SocialLoginType socialLoginType, String idToken) throws BaseException, IOException {
        if (idToken.equals("") || idToken == null) throw new BaseException(EMPTY_ID_TOKEN);
        String email = "";
        if (socialLoginType.equals(SocialLoginType.KAKAO)) email = kakaoService.getKakaoUserInfo(idToken);
        else if(socialLoginType.equals(SocialLoginType.APPLE)) {
            appleService2.getClaimsBy("123");
        }
        String tmp = isUsedEmail(email);
        if (tmp == "using") { // 로그인
            User user = userRepository.findByEmail(email).orElse(null);
            user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
            return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken());
        } else if(tmp == "deleted"){ // 삭제된 계정

        } else {    // 회원 가입
            createUser(email);
        }
        return new LoginRes("123","123");
    }

    @Transactional
    public LoginRes login(LoginReq loginReq) throws BaseException {
        User user = userRepository.findByEmail(loginReq.getEmail()).orElse(null);
        if (user == null || user.isDeleted()) throw new BaseException(USER_IS_NOT_AVAILABLE);
       user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
        return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken());
    }

    public LoginRes renewalAccessToken(RefreshTokenReq refreshTokenReq) throws BaseException {
        String refreshToken = refreshTokenReq.getRefreshToken();
        if (refreshToken.equals("") || refreshToken.length() == 0) throw new BaseException(EMPTY_REFRESH_TOKEN);
        if (!jwtService.verifyJWT(refreshToken)) throw new BaseException(INVALID_TOKEN);
        else {
            Long userId = jwtService.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findByUserId(userId).orElse(null);

            if (refreshToken.equals(user.getRefreshToken()))
                return new LoginRes(jwtService.createJwt(userId), refreshToken);
            else
                throw new BaseException(INVALID_TOKEN);
        }
    }
}
