package com.teenteen.topping.user;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.utils.S3Service;
import com.teenteen.topping.utils.mail.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;
    private final SendEmailService sendEmailService;
    private final S3Service s3Service;
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
    @PostMapping("/{socialLoginType}/login")
    public ResponseEntity socialLogin(@RequestBody SocialLoginReq socialLoginReq,
                                      @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        try{
            return new ResponseEntity(userService.socialLogin(socialLoginType,socialLoginReq.getIdToken()),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }catch (IOException e) {
            return new ResponseEntity(new BaseResponse(BaseResponseStatus.INVALID_TOKEN),
                    HttpStatus.valueOf(400));
        }
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

    @GetMapping("/test")
    public ResponseEntity test(@RequestPart(value = "file", required = true)
                                           MultipartFile multipartFile) throws IOException {
        return new ResponseEntity(s3Service.upload(multipartFile)+System.currentTimeMillis(),HttpStatus.valueOf(200));
    }
}
