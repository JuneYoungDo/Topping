package com.teenteen.topping.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class SignUpReq {
    private Long userId;
    @NotBlank(message = "성별을 입력해주세요.")
    private String gender;
    @NotBlank(message = "생일을 입력해주세요.")
    private String birth;
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
