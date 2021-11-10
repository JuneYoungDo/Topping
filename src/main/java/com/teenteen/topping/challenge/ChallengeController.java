package com.teenteen.topping.challenge;

import com.teenteen.topping.category.CategoryDto.MainCategoryReq;
import com.teenteen.topping.category.CategoryRepository;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ChallengeController {
    private final ChallengeService challengeService;
    private final JwtService jwtService;

    /**
     * 토핑 선택시 동영상 가져오기(시간 순)
     * [GET] /challenge/{challengeId}
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity getVideoListOfChallengeId(@PathVariable Long challengeId) {
        try {
            return new ResponseEntity(challengeService.getVideoListByChallengeId(challengeId)
                    , HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }
}
