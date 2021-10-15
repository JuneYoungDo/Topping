package com.teenteen.topping.Config;

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