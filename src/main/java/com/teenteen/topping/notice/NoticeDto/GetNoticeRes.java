package com.teenteen.topping.notice.NoticeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class GetNoticeRes {
    private String title;
    private String description;
    private boolean isChanged;
}
