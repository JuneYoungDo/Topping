package com.teenteen.topping.challenge;

import com.teenteen.topping.category.CategoryDto.MainCategoryRes;
import com.teenteen.topping.category.CategoryRepository;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.VideoListByChooseRes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public boolean isValidChallengeId(Long challengeId) {
        if(challengeRepository.existsByChallengeId(challengeId) == false)
            return false;
        Challenge challenge = challengeRepository.getById(challengeId);
        if(challenge.isDeleted() == true)
            return false;
        return true;
    }
    public List<VideoListByChooseRes> getVideoListByChallengeId(Long challengeId) throws BaseException {
        if(isValidChallengeId(challengeId) == false)
            throw new BaseException(BaseResponseStatus.INVALID_CHALLENGE);
        List<Video> videoList = challengeRepository
                .getVideoByChallenge(challengeId, PageRequest.of(0,50)).orElse(null);
        List<VideoListByChooseRes> videoListByChooseRes = new ArrayList();
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
            videoListByChooseRes.add(new VideoListByChooseRes(
                    video.getUrl(),
                    video.getUser().getUserId(),
                    video.getUser().getNickname()
            ));
        }
        return videoListByChooseRes;
    }

}
