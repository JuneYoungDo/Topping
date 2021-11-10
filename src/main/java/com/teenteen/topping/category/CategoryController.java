package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.MainCategoryReq;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @GetMapping("/category/{categoryId}")
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
                return new ResponseEntity(200, HttpStatus.valueOf(200));
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
}
