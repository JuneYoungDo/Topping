package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.CategoryListRes;
import com.teenteen.topping.category.CategoryDto.MainCategoryRes;
import com.teenteen.topping.category.CategoryDto.MainFeedRes;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.ChallengeListRes;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.utils.Secret;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.VideoListByCategoryRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryListRes> getCategoryList() {
        return categoryRepository.findByDeleted(false).orElse(null);
    }

    // 카테고리 번호를 이용하여 관련된 동영상 랜덤하게 가져옴
    public List<VideoListByCategoryRes> getRandomVideoByCategoryId(Long categoryId) {
        List<Video> videoList = categoryRepository
                .getVideoByCategory(categoryRepository.getById(categoryId),
                        PageRequest.of(0, 50))  // 50개 까지만
                .orElse(null);
        List<VideoListByCategoryRes> videoListByCategoryRes = new ArrayList();
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
            videoListByCategoryRes.add(new VideoListByCategoryRes(
                    Secret.CLOUD_FRONT_URL + "/" + video.getUrl(),
                    video.getUser().getUserId(),
                    video.getUser().getNickname()
            ));
        }
        return videoListByCategoryRes;
    }

    public MainFeedRes mainFeedCategory(List<Long> picks) {
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

        return new MainFeedRes(returnList, mainFeedTopping(origin));
    }

    public List<ChallengeListRes> mainFeedTopping(List<Long> list) {
        List<Category> categoryList = new ArrayList();
        List<ChallengeListRes> challengeListRes = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            categoryList.add(categoryRepository.getById(list.get(i)));
        }
        List<Challenge> challengeList = categoryRepository.getChallengeByCategory(categoryList)
                .orElse(null);
        for (int i = 0; i < challengeList.size(); i++) {
            Challenge challenge = challengeList.get(i);
            List<Video> videos = categoryRepository
                    .getRecentVideoByChallenge(challenge, PageRequest.of(0, 1))
                    .orElse(null);
            String thumbnailUrl;
            if (videos.size() == 0) thumbnailUrl = null;
            else {
                thumbnailUrl = videos.get(0).getThumbnail();
            }
            challengeListRes.add(new ChallengeListRes(
                    challenge.getChallengeId(),
                    challenge.getName(),
                    thumbnailUrl
            ));
        }
        return challengeListRes;
    }
}
