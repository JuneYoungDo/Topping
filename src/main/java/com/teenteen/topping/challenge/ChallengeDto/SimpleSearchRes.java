package com.teenteen.topping.challenge.ChallengeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class SimpleSearchRes {
    private Long challengeId;
    private String name;
    private List<String> keyWords;
}
