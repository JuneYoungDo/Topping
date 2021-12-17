package com.teenteen.topping.notice;

import com.teenteen.topping.notice.NoticeDto.GetNoticeRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoticeService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final NoticeRepository noticeRepository;

    public GetNoticeRes getNotice() {
        Notice notice = noticeRepository.getById(1L);
        return new GetNoticeRes(notice.getTitle(),notice.getDescription(),notice.isChanged());
    }
}
