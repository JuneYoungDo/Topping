package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.MainCategoryReq;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtService jwtService;

    /**
     * 테스트
     */
    @PostMapping("/ch/test")
    public ResponseEntity test() {

        return new ResponseEntity(200, HttpStatus.valueOf(200));
    }

    /**
     * 카테고리 목록 가져오기
     * [GET] /category
     */
    @GetMapping("/category")
    public ResponseEntity getCategoryList() {
        return new ResponseEntity(categoryService.getCategoryList(), HttpStatus.valueOf(200));
    }

    /**
     * 카테고리 선택시 동영상 가져오기(Random)
     * [GET] /category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity getVideoListOfCategoryId(@PathVariable Long categoryId) {
        return new ResponseEntity(categoryService.getRandomVideoByCategoryId(categoryId)
                , HttpStatus.valueOf(200));
    }

    /**
     * 메인화면
     * [POST] /main
     */
    @PostMapping("/main")
    public ResponseEntity getMainCategory(@RequestBody MainCategoryReq mainCategoryReq) {
        if (mainCategoryReq.getPick1() == null || mainCategoryReq.getPick2() == null
                || mainCategoryReq.getPick3() == null)
            return new ResponseEntity(new BaseResponse(BaseResponseStatus.INVALID_INPUT_NUM),
                    HttpStatus.valueOf(400));
        return new ResponseEntity(categoryService.mainFeedCategory(mainCategoryReq.makePicks()),
                    HttpStatus.valueOf(200));
    }
}
