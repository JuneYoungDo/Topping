package com.teenteen.topping.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SendEmailReq {
    @Email(message = "올바른 형태의 이메일을 입력해주세요.")
    private String email;
}
