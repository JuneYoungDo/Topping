package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.MainCategoryReq;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.video.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(categoryService.mainFeedCategory(mainCategoryReq.makePicks()),
                    HttpStatus.valueOf(200));

        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }
}
