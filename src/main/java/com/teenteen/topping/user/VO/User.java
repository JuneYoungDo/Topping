package com.teenteen.topping.user.VO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;
    private String password;
    private String gender;
    private int age;
    @Column(updatable = false)
    private String birth;
    private int level;
    private String nickname;
    private String refreshToken;
    private boolean isDeleted;
    private Date createdAt;
}
