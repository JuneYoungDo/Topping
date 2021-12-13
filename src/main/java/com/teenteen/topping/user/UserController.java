package com.teenteen.topping.user;

import com.teenteen.topping.category.CategoryDto.MainCategoryReq;
import com.teenteen.topping.category.CategoryService;
import com.teenteen.topping.challenge.ChallengeService;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;
    private final JwtService jwtService;
    private final ChallengeService challengeService;
    private final CategoryService categoryService;
    private final VideoService videoService;

    /**
     * 닉네임 확인
     * [POST] /user/name
     */
    @PostMapping("/user/name")
    public ResponseEntity checkNickname(@RequestBody @Valid NicknameReq nicknameReq, Errors errors) {
        if (errors.hasErrors()) {
            BaseResponseStatus baseResponseStatus = BaseResponseStatus.CUSTOM_ERROR;
            baseResponseStatus.setMessage(errors.getFieldError().getDefaultMessage());
            return new ResponseEntity(new BaseResponse(baseResponseStatus),
                    HttpStatus.valueOf(baseResponseStatus.getStatus()));
        }
        return new ResponseEntity(new NicknameRes(userService.isUsedNickname(nicknameReq.getNickname())),
                HttpStatus.valueOf(200));
    }

    /**
     * 소셜 로그인(KAKAO)
     * [POST] /{socialLoginType}/login
     *
     * @param socialLoginType (KAKAO, APPLE)
     */
    @PostMapping("/{socialLoginType}/login")
    public ResponseEntity socialLogin(@RequestBody SocialLoginReq socialLoginReq,
                                      @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        try {
            return new ResponseEntity(userService.socialLogin(socialLoginType, socialLoginReq.getIdToken()),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        } catch (IOException e) {
            return new ResponseEntity(new BaseResponse(BaseResponseStatus.INVALID_TOKEN),
                    HttpStatus.valueOf(400));
        }
    }

    /**
     * 추가 정보 기입
     * [POST] /info
     */
    @PostMapping("/info")
    public ResponseEntity addBasicInfo(@RequestBody AddBasicInfoReq addBasicInfoReq) {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(userService.editBasicInfo(userId, addBasicInfoReq),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 프로필 사진 바꾸기
     * [POST] /user/info/img
     */
    @PostMapping("/user/info/img")
    public ResponseEntity editUserImg(@RequestBody MultipartFile file) throws IOException {
        try {
            Long userId = jwtService.getUserId();
            System.out.println(userId);
            userService.editProfileImg(userId, file);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 닉네임 바꾸기
     * [PUT] /user/info/nickname
     */
    @PutMapping("/user/info/nickname")
    public ResponseEntity editUserNickname(@RequestBody @Valid NicknameReq nicknameReq, Errors errors) {
        try {
            Long userId = jwtService.getUserId();
            if (errors.hasErrors()) {
                BaseResponseStatus baseResponseStatus = BaseResponseStatus.CUSTOM_ERROR;
                baseResponseStatus.setMessage(errors.getFieldError().getDefaultMessage());
                return new ResponseEntity(new BaseResponse(baseResponseStatus),
                        HttpStatus.valueOf(baseResponseStatus.getStatus()));
            }
            userService.editNickname(userId, nicknameReq.getNickname());
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }


    /**
     * refreshToken을 이용한 accessToken 재발급
     * [GET] /login/refresh
     */
    @GetMapping("/login/refresh")
    public ResponseEntity renewalAccessToken() {
        try {
            String refreshToken = jwtService.getRefreshJwt();
            Long userId = jwtService.getUserIdFromRefreshToken();
            return new ResponseEntity(userService.renewalAccessToken(userId, refreshToken), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 회원 탈퇴하기 (유저정보 삭제)
     * [DELETE] /user
     */
    @DeleteMapping("/user")
    public ResponseEntity deleteUser() {
        try {
            Long userId = jwtService.getUserId();
            userService.deleteUser(userId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 카테고리 가져오기
     * [GET] /user/category
     */
    @GetMapping("/user/category")
    public ResponseEntity getCategory() {
        try {
            System.out.println(LocalDateTime.now());
            if (jwtService.getJwt() == "" || jwtService.getJwt() == null) {
                return new ResponseEntity(categoryService.getCategoryList()
                        , HttpStatus.valueOf(200));
            } else {
                Long userId = jwtService.getUserId();
                return new ResponseEntity(userService.getCategoryListWithLogin(userId),
                        HttpStatus.valueOf(200));
            }
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 카테고리 저장하기(3개 ~ 6개)
     * [POST] /user/category
     */
    @PostMapping("/user/category")
    public ResponseEntity saveUserCategory(@RequestBody MainCategoryReq mainCategoryReq) {
        if (mainCategoryReq.getPicks().size() < 3 || mainCategoryReq.getPicks().size() > 6)
            return new ResponseEntity(new BaseResponse(BaseResponseStatus.INVALID_INPUT_NUM),
                    HttpStatus.valueOf(400));
        try {
            Long userId = jwtService.getUserId();
            userService.saveUserCategory(userId, mainCategoryReq.getPicks());
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 토핑(챌린지) 저장하기
     * [POST] /user/challenge
     */
    @PostMapping("/user/challenge")
    public ResponseEntity saveUserChallenge(@RequestBody UserChallengeReq userChallengeReq) {
        try {
            Long userId = jwtService.getUserId();
            userService.saveUserChallenge(userId, userChallengeReq.getChallengeId());
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 토핑(챌린지) 삭제하기
     * [PUT] /user/challenge
     */
    @PutMapping("/user/challenge")
    public ResponseEntity deleteUserChallenge(@RequestBody UserChallengeReq userChallengeReq) {
        try {
            Long userId = jwtService.getUserId();
            userService.deleteUserChallenge(userId, userChallengeReq.getChallengeId());
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 마이페이지 보기
     * [GET] /user/info
     */
    @GetMapping("/user")
    public ResponseEntity myPage() {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(userService.getUserProfile(userId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 저장한 챌린지 보기
     * [GET] /user/challenges
     */
    @GetMapping("/user/challenges")
    public ResponseEntity myChallenges() {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(challengeService.getUserChallengeList(userId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 나의 피드 보기
     * [GET] /user/feeds
     */
    @GetMapping("/user/feeds")
    public ResponseEntity myFeeds() {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(videoService.getUserFeeds(userId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 내 동영상 삭제하기
     * [DELETE] /user/video/{videoId}
     */
    @DeleteMapping("/user/video/{videoId}")
    public ResponseEntity deleteVideo(@PathVariable Long videoId) {
        try {
            Long userId = jwtService.getUserId();
            userService.deleteVideo(userId, videoId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 동영상에 반응하기
     * [POST] /user/react/{videoId}
     */
    @PostMapping("/user/react/{videoId}")
    public ResponseEntity reactVideo(@PathVariable Long videoId,
                                     @RequestBody UserReactVideoReq userReactVideoReq) {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(
                    userService.reactVideo(userId, videoId, userReactVideoReq.getMode()),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 동영상 반응 가져오기
     * [GET] /video/react/{videoId}
     */
    @GetMapping("/video/react/{videoId}")
    public ResponseEntity reactNum(@PathVariable Long videoId) {
        try {
            if (jwtService.getJwt() == null || jwtService.getJwt() == "") {
                return new ResponseEntity(userService.getReactNum(null, videoId), HttpStatus.valueOf(200));
            } else {
                Long userId = jwtService.getUserId();
                return new ResponseEntity(userService.getReactNum(userId, videoId), HttpStatus.valueOf(200));
            }
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 유저 차단하기
     * [POST] /user/blackList
     */
    @PostMapping("/user/blackList")
    public ResponseEntity blockUser(@RequestBody BlockUserReq blockUserReq) {
        try {
            Long userId = jwtService.getUserId();
            Long blockUserId = blockUserReq.getUserId();
            userService.blockUser(userId, blockUserId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 유저 차단 해제하기
     * [POST] /user/blackList/clear
     */
    @PostMapping("/user/blackList/clear")
    public ResponseEntity clearUser(@RequestBody BlockUserReq blockUserReq) {
        try {
            Long userId = jwtService.getUserId();
            Long blockUserId = blockUserReq.getUserId();
            userService.clearUser(userId, blockUserId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 유저 차단목록 확인하기
     * [GET] /user/blackList
     */
    @GetMapping("/user/blackList")
    public ResponseEntity userBlackList() {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(userService.blockUserList(userId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }


    /**
     * 동영상 차단하기
     * [POST] /video/blackList
     */
    @PostMapping("/video/blackList")
    public ResponseEntity blockVideo(@RequestBody BlockVideoReq blockVideoReq) {
        try {
            Long userId = jwtService.getUserId();
            Long blockVideoId = blockVideoReq.getVideoId();
            userService.blockVideo(userId, blockVideoId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 동영상 차단 해제하기
     * [POST] /video/blackList/clear
     */
    @PostMapping("/video/blackList/clear")
    public ResponseEntity clearVideo(@RequestBody BlockVideoReq blockVideoReq) {
        try {
            Long userId = jwtService.getUserId();
            Long blockVideoId = blockVideoReq.getVideoId();
            userService.clearVideo(userId, blockVideoId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 동영상 차단목록 확인하기
     * [GET] /video/blackList
     */
    @GetMapping("/video/blackList")
    public ResponseEntity videoBlackList() {
        try {
            Long userId = jwtService.getUserId();
            return new ResponseEntity(userService.blockVideoList(userId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 동영상 신고하기
     * [POST] /video/report
     */
    // 방식을 바꿀만함 -> 각 동영상 컬럼에 신고 횟수를 넣는다?
    @PostMapping("/video/report")
    public ResponseEntity videoReport(@RequestBody BlockVideoReq blockVideoReq) {
        try {
            Long userId = jwtService.getUserId();
            Long videoId = blockVideoReq.getVideoId();
            videoService.reportVideo(userId, videoId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 사용자 신고하기
     * [POST] /user/report
     */
    @PostMapping("/user/report")
    public ResponseEntity userReport(@RequestBody BlockUserReq blockUserReq) {
        try {
            Long userId = jwtService.getUserId();
            Long suspicionUserId = blockUserReq.getUserId();
            userService.reportUser(userId, suspicionUserId);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

}
