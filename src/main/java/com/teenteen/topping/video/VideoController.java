package com.teenteen.topping.video;

import com.teenteen.topping.config.BaseException;
import com.teenteen.topping.config.BaseResponse;
import com.teenteen.topping.utils.JwtService;
import com.teenteen.topping.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.jcodec.api.JCodecException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final JwtService jwtService;

    @PostMapping("/upload")
    public ResponseEntity uploadVideo(@RequestPart(value = "file", required = true) MultipartFile multipartFile,
                               @RequestParam(value = "challengeId") Long challengeId) throws IOException {
        try {
            Long userId = jwtService.getUserId();
            videoService.uploadVideo(userId,challengeId,multipartFile);
            return new ResponseEntity(200, HttpStatus.valueOf(200));
        } catch (BaseException exception) {
            return new ResponseEntity(new BaseResponse(exception.getStatus()),
                    HttpStatus.valueOf(exception.getStatus().getStatus()));
        } catch (JCodecException exception) {
            return new ResponseEntity(exception.getMessage(),HttpStatus.valueOf(500));
        }
    }
}
