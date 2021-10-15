package com.teenteen.topping.Config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BaseResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;
    private Timestamp timestamp;
    private int status;       // HTTP STATUS
    private String message;
    private int code;         // CUSTOM CODE

    public BaseResponse(BaseResponseStatus status) {
        this.timestamp = status.getTimestamp();
        this.status = status.getStatus();
        this.message = status.getMessage();
        this.code = status.getCode();
    }
}
