package com.teenteen.topping.user;

import com.teenteen.topping.Config.BaseException;
import com.teenteen.topping.Config.BaseResponse;
import com.teenteen.topping.Config.BaseResponseStatus;
import com.teenteen.topping.user.UserDto.SendEmailReq;
import com.teenteen.topping.user.UserDto.SendEmailRes;
import com.teenteen.topping.user.UserDto.SignUpReq;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.utils.mail.MailDto;
import com.teenteen.topping.utils.mail.SendEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
     * 회원가입
     * [POST] /user
     */
    @PostMapping("/user")
    public ResponseEntity signUp(@RequestBody @Valid SignUpReq signUpReq, Errors errors) {
        if(errors.hasErrors()) {
            BaseResponseStatus baseResponseStatus = BaseResponseStatus.CUSTOM_ERROR;
            baseResponseStatus.setMessage(errors.getFieldError().getDefaultMessage());
            return new ResponseEntity(new BaseResponse(baseResponseStatus),
                    HttpStatus.valueOf(baseResponseStatus.getStatus()));
        }
        try{
            userService.createUser(signUpReq);
            return new ResponseEntity(HttpStatus.valueOf(200));
        } catch (BaseException baseException) {
            return new ResponseEntity(new BaseResponse(baseException.getStatus()),
                    HttpStatus.valueOf(baseException.getStatus().getStatus()));
        }
    }

    /**
     * 회원가입 이메일 인증
     * [POST] /user/email
     */
    @PostMapping("/user/email")
    public ResponseEntity checkEmail(@RequestBody @Valid SendEmailReq sendEmailReq,Errors errors) {
        if(errors.hasErrors()) {
            BaseResponseStatus baseResponseStatus = BaseResponseStatus.CUSTOM_ERROR;
            baseResponseStatus.setMessage(errors.getFieldError().getDefaultMessage());
            return new ResponseEntity(new BaseResponse(baseResponseStatus),
                    HttpStatus.valueOf(baseResponseStatus.getStatus()));
        }
        MailDto mailDto = sendEmailService.createMail(sendEmailReq.getEmail());
        sendEmailService.mailSend(mailDto);
        return new ResponseEntity(new SendEmailRes(mailDto.getStr()),HttpStatus.valueOf(200));
    }

}
