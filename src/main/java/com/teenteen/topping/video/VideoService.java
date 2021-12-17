package com.teenteen.topping.video;

import com.teenteen.topping.challenge.ChallengeRepository;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.S3Service;
import com.teenteen.topping.utils.Secret;
import com.teenteen.topping.video.VO.SuspicionVideo;
import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.UserVideoList;
import com.teenteen.topping.video.VideoDto.VideoListByChooseRes;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.JCodecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.teenteen.topping.config.BaseResponseStatus.INVALID_VIDEO_ID;

@Service
@RequiredArgsConstructor
public class VideoService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final VideoRepository videoRepository;
    private final SuspicionVideoRepository suspicionVideoRepository;

    @Transactional
    public void save(Video video) {
        videoRepository.save(video);
    }

    @Transactional
    public void uploadVideo(Long userId, Long challengeId, MultipartFile file)
            throws JCodecException, IOException, BaseException {
        if (!challengeRepository.existsById(challengeId) ||
                challengeRepository.findById(challengeId).orElse(null).isDeleted())
            throw new BaseException(BaseResponseStatus.INVALID_CHALLENGE);
        User user = userRepository.getById(userId);
        Challenge challenge = challengeRepository.getById(challengeId);
        String fileName = s3Service.uploadVideo(file);
        Video video = Video.builder()
                .url(Secret.CLOUD_FRONT_URL + fileName + "/Default/HLS/" + fileName + "_540.m3u8")
                .thumbnail(Secret.CLOUD_FRONT_URL + fileName + "/Default/Thumbnails/" + fileName + ".0000001.jpg")
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .challenge(challenge)
                .user(user)
                .build();
        save(video);
    }

    public List<UserVideoList> getUserFeeds(Long userId) {
        return videoRepository.getVideosByUserId(userId).orElse(null);
    }

    public boolean isValidVideoId(Long videoId) {
        if (videoRepository.existsById(videoId) == false)
            return false;
        Video video = videoRepository.getById(videoId);
        if (video.isDeleted() == true)
            return false;
        return true;
    }

    public VideoListByChooseRes getVideo(Long videoId) throws BaseException {
        if (!isValidVideoId(videoId)) throw new BaseException(BaseResponseStatus.INVALID_VIDEO_ID);
        Video video = videoRepository.getById(videoId);
        User user = video.getUser();
        Challenge challenge = video.getChallenge();
        return new VideoListByChooseRes(video.getVideoId(), video.getUrl(),
                challenge.getChallengeId(), challenge.getName(),
                user.getUserId(), user.getNickname(), user.getProfileUrl());
    }

    @Transactional
    public void reportVideo(Long userId, Long videoId) throws BaseException {
        if (!isValidVideoId(videoId)) throw new BaseException(INVALID_VIDEO_ID);
        SuspicionVideo suspicionVideo = SuspicionVideo.builder()
                .userId(userId)
                .videoId(videoId)
                .build();
        suspicionVideoRepository.save(suspicionVideo);

        if (suspicionVideoRepository.countReport(videoId).orElse(0L) >= 10) {
            Video video = videoRepository.getById(videoId);
            video.setDeleted(true);
        }
    }
}
