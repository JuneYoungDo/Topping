package com.teenteen.topping.user.VO;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "suspicion_user")
public class SuspicionUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long suspicionId;
    private Long userId;
    private Long suspicionUserId;
}
