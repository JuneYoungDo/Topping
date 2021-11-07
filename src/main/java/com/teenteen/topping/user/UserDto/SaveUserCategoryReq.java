package com.teenteen.topping.user.UserDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SaveUserCategoryReq {
    private Long category1;
    private Long category2;
    private Long category3;
}
