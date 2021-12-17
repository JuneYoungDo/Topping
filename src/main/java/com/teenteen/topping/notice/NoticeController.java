package com.teenteen.topping.notice;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NoticeService noticeService;

    /**
     * 공지 가져오기
     * [GET] /notice
     */
    @GetMapping("/notice")
    public ResponseEntity getNotice() {
        return new ResponseEntity(noticeService.getNotice(), HttpStatus.valueOf(200));
    }

}
