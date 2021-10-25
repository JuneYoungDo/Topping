package com.teenteen.topping.user;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.utils.mail.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final SendEmailService sendEmailService;

    /**
     * 닉네임 확인
     * [POST] /user/name
     */
    @PostMapping("/user/name")
    public ResponseEntity checkNickname(@RequestBody @Valid NicknameReq nicknameReq, Errors errors) {
        if(errors.hasErrors()) {
            BaseResponseStatus baseResponseStatus = BaseResponseStatus.CUSTOM_ERROR;
            baseResponseStatus.setMessage(errors.getFieldError().getDefaultMessage());
            return new ResponseEntity(new BaseResponse(baseResponseStatus),
                    HttpStatus.valueOf(baseResponseStatus.getStatus()));
        }
        return new ResponseEntity(new NicknameRes(userService.isUserNickname(nicknameReq.getNickname())),
                HttpStatus.valueOf(200));
    }

    /**
     * 소셜 로그인(KAKAO)
     * [POST] /{socialLoginType}/login
     * @param socialLoginType (KAKAO, APPLE)
     */
    public ResponseEntity socialLogin(@RequestBody SocialLoginReq socialLoginReq,
                                      @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {

    }


    /**
     * 로그인
     * [POST] /login
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginReq loginReq) {
        try{
            return new ResponseEntity(userService.login(loginReq),HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * refreshToken을 이용한 accessToken 재발급
     * [POST] /login/refresh
     */
    @PostMapping("/login/refresh")
    public ResponseEntity renewalAccessToken(@RequestBody RefreshTokenReq refreshTokenReq) {
        try{
            return new ResponseEntity(userService.renewalAccessToken(refreshTokenReq),HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

}
