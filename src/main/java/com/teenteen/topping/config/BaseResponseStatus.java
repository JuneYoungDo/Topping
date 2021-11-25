package com.teenteen.topping.config;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public enum BaseResponseStatus {

    EMPTY_JWT(400, "JWT가 없습니다.", 101),
    INVALID_JWT(400,"JWT가 유효하지 않습니다.",102),
    USER_IS_NOT_AVAILABLE(400, "사용자가 유효하지 않습니다.", 103),
    CUSTOM_ERROR(400,"",104),
    EXISTS_USER_EMAIL(400,"이미 사용중인 이메일입니다.",105),
    USED_NICKNAME(400,"이미 사용중인 닉네임입니다.",106),
    DO_NOT_MATCH_PASSWORD(400,"정보가 일치하지 않습니다.",107),
    DELETED_EMAIL(400,"탈퇴된 회원입니다.",108),
    EMPTY_REFRESH_TOKEN(400,"refreshToken이 없습니다.",109),
    INVALID_TOKEN(400,"유효하지 않은 토큰입니다.",110),
    EMPTY_ID_TOKEN(400,"id토큰이 없습니다.",111),
    FAILED_TO_APPLE_LOGIN(400,"애플 로그인에 실패하였습니다.",112),
    FAILED_TO_FIND_AVAILABLE_RSA(400,"암호키를 찾지 못하였습니다.",113),
    EMPTY_ACCESS_TOKEN(400,"accessToken이 없습니다.",114),
    INVALID_CHALLENGE(400,"유효하지 않은 토핑 id 입니다.",115),
    INVALID_CATEGORY(400,"유효하지 않은 카테고리 id 입니다.",116),
    INVALID_INPUT_NUM(400,"3개 이상 골라주세요.",117),
    ALREADY_SAVED_CHALLENGE(400,"이미 저장된 챌린지 입니다.",118),
    NOT_SAVED_CHALLENGE(400,"저장되지 않은 챌린지 입니다.",119),


    PASSWORD_ENCRYPTION_ERROR(500, "비밀번호 암호화에 실패하였습니다.", 198),
    PASSWORD_DECRYPTION_ERROR(500, "비밀번호 복호화에 실패하였습니다.", 199);

    private final Timestamp timestamp;
    private final int status;       // HTTP STATUS
    private String message;
    private final int code;         // CUSTOM CODE

    BaseResponseStatus(int status, String message, int code) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
