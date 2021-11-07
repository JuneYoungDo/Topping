package com.teenteen.topping.video;

import com.teenteen.topping.utils.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;
    private final S3Service s3Service;

    @PostMapping("/upload")
    public ResponseEntity test(@RequestPart(value = "file", required = true)
                                       MultipartFile multipartFile) throws IOException {
        return new ResponseEntity(s3Service.upload(multipartFile), HttpStatus.valueOf(200));
    }
}
