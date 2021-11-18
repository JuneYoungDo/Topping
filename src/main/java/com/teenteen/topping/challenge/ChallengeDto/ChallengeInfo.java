package com.teenteen.topping.challenge.ChallengeDto;

import com.teenteen.topping.challenge.VO.KeyWord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ChallengeInfo {
    private String name;
    private String description;
    private List<String> tags;
    private List<String> badges;
    private Long categoryId;
}
