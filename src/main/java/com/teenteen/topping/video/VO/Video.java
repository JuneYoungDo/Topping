package com.teenteen.topping.video.VO;

import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.user.VO.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;
    private String url;
    private String thumbnail;
    private boolean deleted;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
