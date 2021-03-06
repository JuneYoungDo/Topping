package com.teenteen.topping.video;

import com.google.api.Http;
import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.JCodecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VideoController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final VideoService videoService;
    private final JwtService jwtService;

    @PostMapping("/upload/video")
    public ResponseEntity uploadVideo(@RequestPart(value = "file", required = true) MultipartFile multipartFile,
                                      @RequestParam(value = "challengeId") Long challengeId) throws IOException {
        try {
            Long userId = jwtService.getUserId();
            videoService.uploadVideo(userId, challengeId, multipartFile);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        } catch (JCodecException exception) {
            return new ResponseEntity(exception.getMessage(), HttpStatus.valueOf(500));
        }
    }

    /**
     * 동영상 보기
     * [GET] /video/{videoId}
     */
    @GetMapping("/video/{videoId}")
    public ResponseEntity getVideoByVideoId(@PathVariable Long videoId) {
        try {
            return new ResponseEntity(videoService.getVideo(videoId), HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        }
    }
}
