package com.teenteen.topping.challenge.ChallengeDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChallengeListResWithCategory {
    private Long categoryId;
    private String name;
    private String description;
    private List<SimpleSearchRes> challengeList;
}
