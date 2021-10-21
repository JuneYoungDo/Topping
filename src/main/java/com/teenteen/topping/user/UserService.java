package com.teenteen.topping.user;

import com.teenteen.topping.Config.BaseException;
import com.teenteen.topping.user.UserDto.LoginReq;
import com.teenteen.topping.user.UserDto.LoginRes;
import com.teenteen.topping.user.UserDto.RefreshTokenReq;
import com.teenteen.topping.user.UserDto.SignUpReq;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.Bcrypt;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static com.teenteen.topping.Config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final Bcrypt bcrypt;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public void createUser(SignUpReq signUpReq) throws BaseException {
        String tmp = isUsedEmail(signUpReq.getEmail());
        if(tmp == "using") throw new BaseException(EXISTS_USER_EMAIL);
        else if(tmp == "deleted") throw new BaseException(DELETED_EMAIL);
        if(isUserNickname(signUpReq.getNickname())) throw new BaseException(USED_NICKNAME);
        try {
            User user = new User(signUpReq.getUserId(),
                    signUpReq.getGender(),
                    signUpReq.getBirth(),
                    signUpReq.getNickname(),
                    signUpReq.getEmail(),
                    bcrypt.encrypt(signUpReq.getPassword()),
                    0,
                    "",
                    false,
                    new Date()
            );
            save(user);
        } catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
    }

    public boolean isUserNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElse(null);
        if(user != null && user.isDeleted() == false)
            return true;
        else
            return false;
    }

    public String isUsedEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user == null) return "free";
        else if(user.isDeleted() == true) return "deleted";
        else return "using";
    }

    @Transactional
    public LoginRes login(LoginReq loginReq) throws BaseException {
        User user = userRepository.findByEmail(loginReq.getEmail()).orElse(null);
        if(user == null || user.isDeleted()) throw new BaseException(USER_IS_NOT_AVAILABLE);
        if(!comparePwd(loginReq.getPassword(), user.getPassword())) throw new BaseException(DO_NOT_MATCH_PASSWORD);
        user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
        return new LoginRes(jwtService.createJwt(user.getUserId()),user.getRefreshToken());
    }

    public boolean comparePwd(String loginPwd,String dbPwd) {
        return bcrypt.isMatch(loginPwd,dbPwd);
    }

    public LoginRes renewalAccessToken(RefreshTokenReq refreshTokenReq) throws BaseException {
        String refreshToken = refreshTokenReq.getRefreshToken();
        if(refreshToken.equals("")||refreshToken.length()==0) throw new BaseException(EMPTY_REFRESH_TOKEN);
        if(!jwtService.verifyJWT(refreshToken)) throw new BaseException(INVALID_TOKEN);
        else {
            Long userId = jwtService.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findByUserId(userId).orElse(null);

            if(refreshToken.equals(user.getRefreshToken()))
                return new LoginRes(jwtService.createJwt(userId), refreshToken);
            else
                throw new BaseException(INVALID_TOKEN);
        }
    }
}
