package com.teenteen.topping.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class NicknameReq {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 10, message = "최대 10자리까지만 입력해주세요.")
    private String nickname;
}
