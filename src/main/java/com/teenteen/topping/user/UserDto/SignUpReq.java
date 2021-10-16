package com.teenteen.topping.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class SignUpReq {
    private Long userId;
    @NotBlank(message = "이메일을 입력해 주세요.")
    @Email(message = "이메일 형식을 맞춰 주십시오.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "성별을 선택해 주십시오.")
    private String gender;
    private int age;
    private String birth;

    @NotBlank(message = "닉네임을 입력해 주십시오.")
    @Size(min = 1, max = 8, message = "닉네임은 1글자 이상, 8글자 이하로 입력해주세요.")
    private String nickname;
}
