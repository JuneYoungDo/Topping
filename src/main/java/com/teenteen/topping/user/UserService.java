package com.teenteen.topping.user;

import com.teenteen.topping.Config.BaseException;
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
        if(isUsedEmail(signUpReq.getEmail())) throw new BaseException(EXISTS_USER_EMAIL);
        if(isUserNickname(signUpReq.getNickname())) throw new BaseException(USED_NICKNAME);
        try {
            User user = new User(signUpReq.getUserId(),
                    signUpReq.getEmail(),
                    bcrypt.encrypt(signUpReq.getPassword()),
                    signUpReq.getGender(),
                    signUpReq.getAge(),
                    signUpReq.getBirth(),
                    0,
                    signUpReq.getNickname(),
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

    public boolean isUsedEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user != null && user.isDeleted()==false)  // 사용자가 있고 삭제가 안되어 있다면 사용 불가
            return true;
        else
            return false;
    }
}
