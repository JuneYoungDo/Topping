package com.teenteen.topping.video.VO;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Entity(name = "suspicion_video")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuspicionVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suspicionId;
    private Long userId;
    private Long videoId;
}
