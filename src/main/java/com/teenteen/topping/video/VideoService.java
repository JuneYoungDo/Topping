package com.teenteen.topping.video;

import com.teenteen.topping.challenge.ChallengeRepository;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponseStatus;
import com.teenteen.topping.user.UserRepository;
import com.teenteen.topping.user.UserService;
import com.teenteen.topping.user.VO.User;
import com.teenteen.topping.utils.S3Service;
import com.teenteen.topping.utils.Secret;
import com.teenteen.topping.video.VO.Video;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.JCodecException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    private final VideoRepository videoRepository;

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
        List<String> urlList = s3Service.uploadVideoWithThumbnail(file);
        Video video = Video.builder()
                .url(Secret.CLOUD_FRONT_URL + urlList.get(0))
                .thumbnail(Secret.CLOUD_FRONT_URL + urlList.get(1))
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .challenge(challenge)
                .user(user)
                .build();
        save(video);
    }
}