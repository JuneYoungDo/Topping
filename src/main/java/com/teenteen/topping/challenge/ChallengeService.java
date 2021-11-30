package com.teenteen.topping.challenge;

import com.teenteen.topping.challenge.ChallengeDto.ChallengeInfo;
import com.teenteen.topping.challenge.ChallengeDto.UserChallengeRes;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.VideoListByChooseRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;

    public boolean isValidChallengeId(Long challengeId) {
        if (challengeRepository.existsByChallengeId(challengeId) == false)
            return false;
        Challenge challenge = challengeRepository.getById(challengeId);
        if (challenge.isDeleted() == true)
            return false;
        return true;
    }

    public List<VideoListByChooseRes> getVideoListByChallengeId(Long challengeId) throws BaseException {
        if (isValidChallengeId(challengeId) == false)
            throw new BaseException(BaseResponseStatus.INVALID_CHALLENGE);
        List<Video> videoList = challengeRepository
                .getVideoByChallenge(challengeId, PageRequest.of(0, 50)).orElse(null);
        List<VideoListByChooseRes> videoListByChooseRes = new ArrayList();
        for (int i = 0; i < videoList.size(); i++) {
            Video video = videoList.get(i);
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

    @Transactional
    public ChallengeInfo getChallengeByChallengeId(Long challengeId) throws BaseException {
        if (isValidChallengeId(challengeId) == false)
            throw new BaseException(BaseResponseStatus.INVALID_CHALLENGE);
        Challenge challenge = challengeRepository.getById(challengeId);
        challenge.setViewCount(challenge.getViewCount() + 1);
        List<String> tags = new ArrayList();
        for (int i = 0; i < challenge.getKeyWords().size(); i++)
            tags.add(challenge.getKeyWords().get(i).getWord());
        return new ChallengeInfo(challenge.getName(),
                challenge.getDescription(),
                tags,
                null,
                challenge.getCategory().getCategoryId()
        );
    }

    public List<UserChallengeRes> getUserChallengeList(Long userId) {
        User user = userRepository.getById(userId);
        List<UserChallengeRes> challengeResList = new ArrayList();
        List<Challenge> challengeList = user.getChallenges();
        Collections.reverse(challengeList);
        for(int i=0;i<challengeList.size();i++) {
            Challenge challenge = challengeList.get(i);
            List<String> keyWords = new ArrayList();
            for(int j=0;j<challenge.getKeyWords().size();j++) {
                keyWords.add(challenge.getKeyWords().get(j).getWord());
            }
            UserChallengeRes userChallengeRes = new UserChallengeRes(
                    challenge.getCategory().getCategoryId(),
                    challenge.getChallengeId(),
                    challenge.getName(),keyWords);
            challengeResList.add(userChallengeRes);
        }
        return challengeResList;
    }

}
