package com.teenteen.topping.challenge.ChallengeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UserChallengeRes {
    private Long challengeId;
    private String challengeName;
    private List<String> keyWords;
}
