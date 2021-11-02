package com.teenteen.topping.category.CategoryDto;

import com.teenteen.topping.challenge.ChallengeDto.ChallengeListRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class MainFeedRes {
    private List<MainCategoryRes> mainCategoryResList;
    private List<ChallengeListRes> challengeListResList;
}
