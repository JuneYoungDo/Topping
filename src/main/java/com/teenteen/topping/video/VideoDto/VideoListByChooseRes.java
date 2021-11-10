package com.teenteen.topping.video.VideoDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class VideoListByChooseRes {
    private String url;
    private Long userId;
    private String userNickName;
}
