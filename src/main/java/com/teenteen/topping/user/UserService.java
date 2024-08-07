package com.teenteen.topping.user;

import com.teenteen.topping.category.CategoryDto.CategoryListRes;
import com.teenteen.topping.category.CategoryRepository;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.SearchChallengeRes;
import com.teenteen.topping.challenge.ChallengeRepository;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.challenge.VO.KeyWord;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.oauth.OauthService.AppleService;
import com.teenteen.topping.oauth.OauthService.KakaoService;
import com.teenteen.topping.oauth.helper.SocialLoginType;
import com.teenteen.topping.user.UserDto.*;
import com.teenteen.topping.user.VO.LikeList;
import com.teenteen.topping.user.VO.SuspicionUser;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.utils.S3Service;
import com.teenteen.topping.utils.Secret;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoRepository;
import com.teenteen.topping.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.teenteen.topping.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final LikeListRepository likeListRepository;
    private final CategoryRepository categoryRepository;
    private final ChallengeRepository challengeRepository;
    private final VideoRepository videoRepository;
    private final JwtService jwtService;
    private final S3Service s3Service;
    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final VideoService videoService;
    private final SuspicionUserRepository suspicionUserRepository;

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
    public void deleteUser(Long userId) {
        User user = userRepository.getById(userId);
        userRepository.deleteUserByUserId(userId);
    }

    @Transactional
    public AddBasicInfoRes editBasicInfo(Long userId, AddBasicInfoReq addBasicInfoReq) {
        User user = userRepository.getById(userId);
        user.setBirth(addBasicInfoReq.getBirth());
        user.setNickname(addBasicInfoReq.getNickName());
        return new AddBasicInfoRes(user.getUserId(), user.getEmail(), user.getBirth(), user.getNickname());
    }

    @Transactional
    public void editNickname(Long userId, String nickname) {
        User user = userRepository.getById(userId);
        user.setNickname(nickname);
    }

    @Transactional
    public void editProfileImg(Long userId, MultipartFile multipartFile) throws IOException {
        User user = userRepository.getById(userId);
        String fileName = s3Service.uploadImg(multipartFile);
        user.setProfileUrl(Secret.CLOUD_FRONT_URL + "profile/" + fileName);
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
            email = appleService.userEmailFromApple(idToken);
        }
        String tmp = isUsedEmail(email);
        if (tmp == "using") { // 로그인
            User user = userRepository.findByEmail(email).orElse(null);
            user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
            return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken(),
                    user.getNickname(), user.getBirth());
//        } else if (tmp == "deleted") { // 삭제된 계정 -> 추후 처리
//            return new LoginRes("Deleted", "Deleted",);
        } else {    // 회원 가입
            createUser(email);
            User user = userRepository.findByEmail(email).orElse(null);
            user.setRefreshToken(jwtService.createRefreshToken(user.getUserId()));
            return new LoginRes(jwtService.createJwt(user.getUserId()), user.getRefreshToken(),
                    user.getNickname(), null);
        }
    }

    public LoginRes renewalAccessToken(Long userId, String refreshToken) throws BaseException {
        if (refreshToken.equals("") || refreshToken.length() == 0) throw new BaseException(EMPTY_REFRESH_TOKEN);
        if (!jwtService.verifyRefreshJWT(refreshToken)) throw new BaseException(INVALID_TOKEN);
        else {
            User user = userRepository.getById(userId);

            if (refreshToken.equals(user.getRefreshToken()))
                return new LoginRes(jwtService.createJwt(userId), refreshToken, user.getNickname(), user.getBirth());
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
        User user = userRepository.getById(userId);
        List<Category> categories = new ArrayList();
        for (int i = 0; i < picks.size(); i++) {
            categories.add(categoryRepository.getById(picks.get(i)));
        }
        user.setCategories(categories);
    }

    @Transactional
    public void saveUserChallenge(Long userId, Long challengeId) throws BaseException {
        User user = userRepository.getById(userId);
        Challenge challenge = challengeRepository.getById(challengeId);
        if (user.getChallenges().contains(challenge))
            throw new BaseException(ALREADY_SAVED_CHALLENGE);
        user.getChallenges().add(challenge);
    }

    @Transactional
    public void deleteUserChallenge(Long userId, Long challengeId) throws BaseException {
        User user = userRepository.getById(userId);
        Challenge challenge = challengeRepository.getById(challengeId);
        if (!user.getChallenges().contains(challenge))
            throw new BaseException(NOT_SAVED_CHALLENGE);
        user.getChallenges().remove(challenge);
    }

    public UserProfileRes getUserProfile(Long userId) {
        return userRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public ReactVideoRes reactVideo(Long userId, Long videoId, Long mode) throws BaseException {
        if (!(mode == 1 || mode == 2 || mode == 3)) throw new BaseException(INVALID_REACT);
        if (!videoService.isValidVideoId(videoId)) throw new BaseException(INVALID_VIDEO_ID);
        Long existed = userRepository.existedReact(userId, videoId).orElse(null);
        String react = "";
        if (mode == 1) {
            react = "good";
        } else if (mode == 2) {
            react = "fire";
        } else if (mode == 3) {
            react = "face";
        }
        if (existed == null) {
            LikeList likeList = LikeList.builder()
                    .mode(mode)
                    .user(userRepository.getById(userId))
                    .video(videoRepository.getById(videoId))
                    .build();
            likeListRepository.save(likeList);
            return new ReactVideoRes(react);
        }
        if (existed == mode) {
            userRepository.editReact(userId, videoId, 0L);
            return new ReactVideoRes("nothing");
        } else {
            userRepository.editReact(userId, videoId, mode);
            return new ReactVideoRes(react);
        }
    }

    public ReactNumRes getReactNum(Long userId, Long videoId) throws BaseException {
        if (!videoService.isValidVideoId(videoId)) throw new BaseException(INVALID_VIDEO_ID);
        //999이상 처리하기
        Long cGood = likeListRepository.countGood(videoId).orElse(0L);
        if (cGood >= 999) cGood = 999L;
        Long cFire = likeListRepository.countFire(videoId).orElse(0L);
        if (cFire >= 999) cFire = 999L;
        Long cFace = likeListRepository.countFace(videoId).orElse(0L);
        if (cFace >= 999) cFace = 999L;
        if (userId != null)
            return new ReactNumRes(cGood, cFire, cFace,
                    likeListRepository.getMode(userId, videoId).orElse(0L));
        else
            return new ReactNumRes(cGood, cFire, cFace, 0L);
    }

    @Transactional
    public void deleteVideo(Long userId, Long videoId) throws BaseException {
        if (!videoService.isValidVideoId(videoId)) throw new BaseException(INVALID_VIDEO_ID);
        User user = userRepository.getById(userId);
        Video video = videoRepository.getById(videoId);
        if (!user.getVideos().contains(video)) throw new BaseException(ITS_NOT_YOUR_VIDEO);
        video.setDeleted(true);
    }

    @Transactional
    public void blockUser(Long userId, Long blockUserId) throws BaseException {
        if (jwtService.isValidUser(blockUserId) == false) throw new BaseException(USER_IS_NOT_AVAILABLE);
        User user = userRepository.getById(userId);
        User blockUser = userRepository.getById(blockUserId);
        if (user == blockUser) throw new BaseException(ITS_YOURSELF);
        if (user.getBlackList().contains(blockUser)) throw new BaseException(ALREADY_BLOCKED_USER);
        List<User> blackList = user.getBlackList();
        blackList.add(blockUser);
        user.setBlackList(blackList);
    }

    @Transactional
    public void clearUser(Long userId, Long blockUserId) throws BaseException {
        if (jwtService.isValidUser(blockUserId) == false) throw new BaseException(USER_IS_NOT_AVAILABLE);
        User user = userRepository.getById(userId);
        User blockUser = userRepository.getById(blockUserId);
        if (!user.getBlackList().contains(blockUser)) throw new BaseException(ALREADY_CLEAR_USER);
        List<User> blackList = user.getBlackList();
        blackList.remove(blockUser);
        user.setBlackList(blackList);
    }

    public List<BlockUserRes> blockUserList(Long userId) {
        User user = userRepository.getById(userId);
        List<User> blackList = user.getBlackList();
        List<BlockUserRes> blockUserResList = new ArrayList<>();
        for (int i = 0; i < blackList.size(); i++) {
            blockUserResList.add(new BlockUserRes(blackList.get(i).getUserId(),
                    blackList.get(i).getNickname()));
        }
        return blockUserResList;
    }

    @Transactional
    public void blockVideo(Long userId, Long blockVideoId) throws BaseException {
        if (videoService.isValidVideoId(blockVideoId) == false) throw new BaseException(INVALID_VIDEO_ID);
        User user = userRepository.getById(userId);
        Video video = videoRepository.getById(blockVideoId);
        if (user.getVideos().contains(video)) throw new BaseException(ITS_YOUR_VIDEO);
        if (user.getBlockVideos().contains(video)) throw new BaseException(ALREADY_BLOCKED_VIDEO);
        List<Video> blockVideoList = user.getBlockVideos();
        blockVideoList.add(video);
        user.setBlockVideos(blockVideoList);
    }

    @Transactional
    public void clearVideo(Long userId, Long blockVideoId) throws BaseException {
        if (!videoService.isValidVideoId(blockVideoId)) throw new BaseException(INVALID_VIDEO_ID);
        User user = userRepository.getById(userId);
        Video video = videoRepository.getById(blockVideoId);
        if (!user.getBlockVideos().contains(video)) throw new BaseException(ALREADY_CLEAR_VIDEO);
        List<Video> blockVideoList = user.getBlockVideos();
        blockVideoList.remove(video);
        user.setBlockVideos(blockVideoList);
    }

    public List<BlockVideoRes> blockVideoList(Long userId) {
        User user = userRepository.getById(userId);
        List<Video> videoBlockList = user.getBlockVideos();
        List<BlockVideoRes> blockVideoResList = new ArrayList<>();
        for (int i = 0; i < videoBlockList.size(); i++) {
            blockVideoResList.add(new BlockVideoRes(videoBlockList.get(i).getVideoId(),
                    videoBlockList.get(i).getThumbnail()));
        }
        return blockVideoResList;
    }

    @Transactional
    public void reportUser(Long userId, Long suspicionUserId) throws BaseException {
        if (!jwtService.isValidUser(suspicionUserId)) throw new BaseException(USER_IS_NOT_AVAILABLE);
        SuspicionUser suspicionUser = SuspicionUser.builder()
                .userId(userId)
                .suspicionUserId(suspicionUserId)
                .build();

        suspicionUserRepository.save(suspicionUser);

        if (suspicionUserRepository.countReport(suspicionUserId).orElse(0L) >= 10) {
            Video video = videoRepository.getById(suspicionUserId);
            video.setDeleted(true);
        }
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
        for (int i = 0; i < challengeList.size(); i++) {
            searchChallengeRes.add(new SearchChallengeRes(challengeList.get(i).getChallengeId(),
                    challengeList.get(i).getName(),
                    challengeList.get(i).getCategory().getCategoryId()));
        }
        return searchChallengeRes;
    }


}
