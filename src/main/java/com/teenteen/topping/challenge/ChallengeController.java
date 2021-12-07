package com.teenteen.topping.challenge;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChallengeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChallengeService challengeService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * 토핑 선택시 동영상 가져오기(시간 순)
     * [GET] /videos/challenge/{challengeId}
     */
    @GetMapping("/videos/challenge/{challengeId}")
    public ResponseEntity getVideoListOfChallengeId(@PathVariable Long challengeId) {
        try {
            if (jwtService.getJwt() == null || jwtService.getJwt() == "") {
                return new ResponseEntity(challengeService.getVideoListByChallengeId(null, challengeId)
                        , HttpStatus.valueOf(200));
            } else {
                Long userId = jwtService.getUserId();
                User user = userRepository.getById(userId);
                return new ResponseEntity(challengeService.getVideoListByChallengeId(user, challengeId)
                        , HttpStatus.valueOf(200));
            }
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

    /**
     * 토핑 선택시 토핑 정보 가져오기
     * [GET] /challenge/{challengeId}
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity getChallengeByChallengeId(@PathVariable Long challengeId) {
        try {
            return new ResponseEntity(challengeService.getChallengeByChallengeId(challengeId),
                    HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }

}
