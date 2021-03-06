package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.CategoryListRes;
import com.teenteen.topping.category.CategoryDto.MainCategoryRes;
import com.teenteen.topping.category.CategoryDto.MainFeedRes;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.ChallengeListRes;
import com.teenteen.topping.challenge.ChallengeDto.ChallengeListResWithCategory;
import com.teenteen.topping.challenge.ChallengeDto.SimpleSearchRes;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.VideoListByChooseRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryRepository categoryRepository;

    public List<CategoryListRes> getCategoryList() {
        return categoryRepository.findByDeleted(false).orElse(null);
    }

    public boolean isValidCategoryId(Long categoryId) {
        if (categoryRepository.existsByCategoryId(categoryId) == false)
            return false;
        Category category = categoryRepository.getById(categoryId);
        if (category.isDeleted() == true)
            return false;
        return true;
    }

    // 카테고리 번호를 이용하여 관련된 동영상 랜덤하게 가져옴
    public List<VideoListByChooseRes> getRandomVideoByCategoryId(User user, Long categoryId) throws BaseException {
        if (isValidCategoryId(categoryId) == false)
            throw new BaseException(BaseResponseStatus.INVALID_CATEGORY);
        List<Video> videoList = categoryRepository
                .getVideoByCategory(categoryRepository.getById(categoryId),
                        PageRequest.of(0, 50))  // 50개 까지만
                .orElse(null);

        List<VideoListByChooseRes> videoListByChooseRes = new ArrayList();
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
            User videoUser = video.getUser();
            if (user != null && user.getBlackList().contains(videoUser)) continue;
            if (user != null && user.getBlockVideos().contains(video)) continue;
            videoListByChooseRes.add(new VideoListByChooseRes(
                    video.getVideoId(),
                    video.getUrl(),
                    video.getChallenge().getChallengeId(),
                    video.getChallenge().getName(),
                    video.getUser().getUserId(),
                    video.getUser().getNickname(),
                    video.getUser().getProfileUrl()
            ));
        }
        return videoListByChooseRes;
    }

    public MainFeedRes mainFeedCategory(User user, List<Long> picks) {
        List<Long> origin = new ArrayList();
        for (int i = 0; i < picks.size(); i++) {
            origin.add(picks.get(i));
        }
        List<MainCategoryRes> returnList = new ArrayList<>();
        // 카테고리 목록 가져오기(delete되지 않은 것만)
        List<CategoryListRes> categories = categoryRepository.findByDeleted(false).orElse(null);
        Map<Long, CategoryListRes> categoryMap = new HashMap<>();

        // 가져온 목록을 map에 넣는다.
        for (int i = 0; i < categories.size(); i++) {
            categoryMap.put(categories.get(i).getCategoryId(), categories.get(i));
        }
        CategoryListRes category = null;
        // map에서 선택된 번호는 바로 returnList에 넣어주고 map에서 삭제
        for (int i = 0; i < picks.size(); i++) {
            category = categoryMap.get(picks.get(i));
            returnList.add(new MainCategoryRes(category.getCategoryId(), true));
            categoryMap.remove(picks.get(i));
        }
        // 남아있는 map에서 랜덤하게 남은 갯수만큼 가져온다
        for (int i = 0; i < 6 - picks.size(); i++) {
            Object[] keys = categoryMap.keySet().toArray();
            Long randomKey = (Long) keys[new Random().nextInt(keys.length)];
            category = categoryMap.get(randomKey);
            origin.add(randomKey);
            returnList.add(new MainCategoryRes(category.getCategoryId(), false));
            categoryMap.remove(randomKey);
        }

        return new MainFeedRes(returnList, mainFeedTopping(user, origin));
    }

    public List<ChallengeListRes> mainFeedTopping(User user, List<Long> list) {
        List<Category> categoryList = new ArrayList();
        List<ChallengeListRes> challengeListRes = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            categoryList.add(categoryRepository.getById(list.get(i)));
        }
        List<Challenge> challengeList = categoryRepository.getChallengesByCategory(categoryList)
                .orElse(null);
        int maxList;
        if (challengeList.size() > 50) maxList = 50;
        else maxList = challengeList.size();
        for (int i = 0; i < maxList; i++) {
            Challenge challenge = challengeList.get(i);
            List<Video> videos = categoryRepository
                    .getRecentVideoByChallenge(challenge, PageRequest.of(0, 50))
                    .orElse(null);
            String thumbnailUrl = null;
            if (videos.size() == 0) thumbnailUrl = null;
            else {
                if (user == null) thumbnailUrl = videos.get(0).getThumbnail();
                else {
                    for (int j = 0; j < videos.size(); j++) {
                        Video video = videos.get(j);
                        if (user.getBlackList().contains(video.getUser()) == true ||
                                user.getBlockVideos().contains(video) == true) {
                            videos.remove(video);
                            j--;
                        }
                    }
                    if (videos.size() == 0) thumbnailUrl = null;
                    else thumbnailUrl = videos.get(0).getThumbnail();
                }
            }
            challengeListRes.add(new ChallengeListRes(
                    challenge.getChallengeId(),
                    challenge.getName(),
                    thumbnailUrl
            ));
        }
        return challengeListRes;
    }

    public List<String> getKeyWordsFromChallenge(Challenge challenge) {
        List<String> keyWords = new ArrayList<>();
        for (int i = 0; i < challenge.getKeyWords().size(); i++) {
            keyWords.add(challenge.getKeyWords().get(i).getWord());
        }
        return keyWords;
    }

    public ChallengeListResWithCategory getChallengesByCategory(Long categoryId, Long sortMethod) throws BaseException {
        if (isValidCategoryId(categoryId) == false) throw new BaseException(BaseResponseStatus.INVALID_CATEGORY);
        Category category = categoryRepository.getById(categoryId);
        List<Challenge> challengeList = new ArrayList<>();
        if (sortMethod == 2) {
            challengeList = categoryRepository.getChallengesByCategorySortWithViewCount(categoryId).orElse(null);
        } else {
            challengeList = categoryRepository.getChallengesByCategorySortWithTime(categoryId).orElse(null);
        }
        List<SimpleSearchRes> simpleSearchResList = new ArrayList();
        for (int i = 0; i < challengeList.size(); i++) {
            Challenge challenge = challengeList.get(i);
            simpleSearchResList.add(new SimpleSearchRes(challenge.getChallengeId(),
                    challenge.getName(), getKeyWordsFromChallenge(challenge)));
        }
        return new ChallengeListResWithCategory(categoryId, category.getName(), category.getDescription(),
                simpleSearchResList);
    }
}
