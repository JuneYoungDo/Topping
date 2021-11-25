package com.teenteen.topping.challenge.VO;

import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.SearchChallengeRes;
import com.teenteen.topping.video.VO.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name = "challenge")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long challengeId;
    private String name;
    private String description;
    @ColumnDefault("0")
    private Long viewCount;
    @ColumnDefault("false")
    private boolean deleted;
    @ColumnDefault("LocalDateTime.now()")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "challenge")
    private List<Video> videos;

    @ManyToMany(mappedBy = "challenges")
    private List<KeyWord> keyWords;

}
