package com.teenteen.topping.category;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * 테스트
     */
    @PostMapping("/ch/test")
    public ResponseEntity test() {

        return new ResponseEntity(200, HttpStatus.valueOf(200));
    }

    /**
     * 카테고리 선택시 동영상 가져오기(Random)
     * [GET] /category/{categoryId}
     */
    @GetMapping("/videos/category/{categoryId}")
    public ResponseEntity getVideoListOfCategoryId(@PathVariable Long categoryId) {
        try {
            return new ResponseEntity(categoryService.getRandomVideoByCategoryId(categoryId)
                    , HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 메인화면
     * [GET] /main
     */
    @GetMapping("/main")
    public ResponseEntity getMainCategory() {
        try {
            // 추후 예정
            if (jwtService.getJwt() == null || jwtService.getJwt() == "") {
                List<Long> picks = new ArrayList(Arrays.asList(2L,3L,5L,7L,4L,11L));
                return new ResponseEntity(categoryService.mainFeedCategory(picks), HttpStatus.valueOf(200));
            } else {
                Long userId = jwtService.getUserId();
                User user = userRepository.getById(userId);
                List<Long> picks = new ArrayList();
                for (int i = 0; i < user.getCategories().size(); i++)
                    picks.add(user.getCategories().get(i).getCategoryId());
                return new ResponseEntity(categoryService.mainFeedCategory(picks),
                        HttpStatus.valueOf(200));
            }
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 카테고리 선택시 해당 토핑들 가져오기
     * [GET] /challenges/category/{categoryId}/{sortMethod}
     * {sortMethod} : 2. 인기순 그 외 시간 순
     */
    @GetMapping("/challenges/category/{categoryId}/{sortMethod}")
    public ResponseEntity getChallenges(@PathVariable Long categoryId, @PathVariable Long sortMethod) {
        try {
            return new ResponseEntity(categoryService.getChallengesByCategory(categoryId,sortMethod),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }
}
