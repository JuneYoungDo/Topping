package com.teenteen.topping.user;

import com.teenteen.topping.category.CategoryDto.CategoryListRes;
import com.teenteen.topping.category.CategoryRepository;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.SearchChallengeRes;
import com.teenteen.topping.challenge.ChallengeRepository;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.challenge.VO.KeyWord;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.oauth.OauthService.AppleService2;
import com.teenteen.topping.oauth.OauthService.KakaoService;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static com.teenteen.topping.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ChallengeRepository challengeRepository;
    private final JwtService jwtService;
    private final KakaoService kakaoService;
    private final AppleService2 appleService2;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public void createUser(String email) {
        User user = User.builder()
                .email(email)
                .birth(null)
                .nickname(RandomStringUtils.random(10, true, true))
                .profileUrl("")
                .refreshToken("")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        save(user);
    }

    @Transactional
    public AddBasicInfoRes editBasicInfo(Long userId, AddBasicInfoReq addBasicInfoReq) {
        User user = userRepository.findByUserId(userId).orElse(null);
        user.setBirth(addBasicInfoReq.getBirth());
        user.setNickname(addBasicInfoReq.getNickName());
        return new AddBasicInfoRes(user.getUserId(), user.getEmail(), user.getBirth(), user.getNickname());
    }

    public boolean isUsedNickname(String nickname) {
        User user = userRepository.findByNickname(nickname).orElse(null);
        if (user != null && user.isDeleted() == false)
            return true;
        else
            return false;
    }

    public String isUsedEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return "free";
        else if (user.isDeleted() == true) return "deleted";
        else return "using";
    }

    @Transactional
    public LoginRes socialLogin(SocialLoginType socialLoginType, String idToken) throws BaseException, IOException {
        if (idToken.equals("") || idToken == null) throw new BaseException(EMPTY_ID_TOKEN);
        String email = "";
        if (socialLoginType.equals(SocialLoginType.KAKAO)) email = kakaoService.getKakaoUserInfo(idToken);
        else if (socialLoginType.equals(SocialLoginType.APPLE)) {
            appleService2.getClaimsBy("123");
        }
        String tmp = isUsedEmail(email);
        if (tmp == "using") { // 로그인
            User user = userRepository.findByEmail(email).orElse(null);
            user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
            return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken());
        } else if (tmp == "deleted") { // 삭제된 계정 -> 추후 처리
            return new LoginRes("Deleted", "Deleted");
        } else {    // 회원 가입
            createUser(email);
            User user = userRepository.findByEmail(email).orElse(null);
            user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
            return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken());
        }
    }

    public LoginRes renewalAccessToken(RefreshTokenReq refreshTokenReq) throws BaseException {
        String refreshToken = refreshTokenReq.getRefreshToken();
        if (refreshToken.equals("") || refreshToken.length() == 0) throw new BaseException(EMPTY_REFRESH_TOKEN);
        if (!jwtService.verifyRefreshJWT(refreshToken)) throw new BaseException(INVALID_TOKEN);
        else {
            Long userId = jwtService.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findByUserId(userId).orElse(null);

            if (refreshToken.equals(user.getRefreshToken()))
                return new LoginRes(jwtService.createJwt(userId), refreshToken);
            else
                throw new BaseException(INVALID_TOKEN);
        }
    }

    public List<GetUserCategoryListRes> getCategoryListWithLogin(Long userId) {
        List<GetUserCategoryListRes> userCategoryList = new ArrayList();
        User user = userRepository.getById(userId);
        List<CategoryListRes> categories = categoryRepository.
                findByDeleted(false).orElse(null);

        Map<Long, Boolean> categoryMap = new HashMap();
        for (int i = 0; i < user.getCategories().size(); i++) {
            categoryMap.put(user.getCategories().get(i).getCategoryId(), true);
        }
        boolean isPicked;
        for (int i = 0; i < categories.size(); i++) {
            isPicked = false;
            if (categoryMap.containsKey(categories.get(i).getCategoryId())) isPicked = true;
            userCategoryList.add(new GetUserCategoryListRes(
                    categories.get(i).getCategoryId(),
                    isPicked
            ));
        }
        return userCategoryList;
    }

    @Transactional
    public void saveUserCategory(Long userId, List<Long> picks) {
        User user = userRepository.findByUserId(userId).orElse(null);
        List<Category> categories = new ArrayList();
        for (int i = 0; i < picks.size(); i++) {
            categories.add(categoryRepository.getById(picks.get(i)));
        }
        user.setCategories(categories);
    }


    //챌린지 검색하기
    public List<SearchChallengeRes> searchChallengeWithKeyWord(String searchWord) {
        // 리턴
        List<SearchChallengeRes> searchChallengeRes = new ArrayList();
        if (searchWord == "" || searchWord == null) return searchChallengeRes;

        // 챌린지 목록
        List<Challenge> challengeList = new ArrayList();
        Map<Challenge, Boolean> challengeMap = new HashMap();
        // 챌린지 이름으로 검색
        List<Challenge> challenges = challengeRepository.searchChallenge(searchWord).orElse(null);
        for (int i = 0; i < challenges.size(); i++) {
            challengeMap.put(challenges.get(i), true);
            challengeList.add(challenges.get(i));
        }
        // 키워드 이름으로 검색
        KeyWord keyWord = challengeRepository.searchKeyWord(searchWord).orElse(null);
        // 키워드 중 검색어가 있다면
        if (keyWord != null) {
            List<Challenge> keyWordChallenges = keyWord.getChallenges();
            for (int i = 0; i < keyWordChallenges.size(); i++) {
                if (challengeMap.containsKey(keyWordChallenges.get(i))) continue;
                else {
                    challengeList.add(keyWordChallenges.get(i));
                }
            }
        }
        for (int i=0;i<challengeList.size();i++) {
            searchChallengeRes.add(new SearchChallengeRes(challengeList.get(i).getChallengeId(),
                    challengeList.get(i).getName(),
                    challengeList.get(i).getCategory().getCategoryId()));
        }
        return searchChallengeRes;
    }


}
