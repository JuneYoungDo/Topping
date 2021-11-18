package com.teenteen.topping.user.VO;

import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.video.VO.Video;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;
    private String birth;
    private String nickname;
    private String profileUrl;
    private String refreshToken;
    private boolean deleted;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user")
    private List<Video> videos;

    @ManyToMany
    @JoinTable(name = "user_category",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;
}
