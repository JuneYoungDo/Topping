package com.teenteen.topping.user;

import com.teenteen.topping.Config.BaseException;
import com.teenteen.topping.Config.BaseResponse;
import com.teenteen.topping.Config.BaseResponseStatus;
import com.teenteen.topping.user.UserDto.SignUpReq;
import com.teenteen.topping.utils.JwtService;
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

    /**
     * 회원가입
     * [POST] /user
     */
    @PostMapping("/user")
    public ResponseEntity signUp(@RequestBody @Valid SignUpReq signUpReq, Errors errors) throws BaseException {
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

}
