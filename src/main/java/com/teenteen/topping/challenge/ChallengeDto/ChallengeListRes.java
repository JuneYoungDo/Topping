package com.teenteen.topping.challenge.ChallengeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChallengeListRes {
    private Long challengeId;
    private String challengeName;
}
