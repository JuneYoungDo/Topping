package com.teenteen.topping.user.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String gender;
    private String birth;
    private String nickname;

    private String email;
    private String password;

    private int level;
    private String refreshToken;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
