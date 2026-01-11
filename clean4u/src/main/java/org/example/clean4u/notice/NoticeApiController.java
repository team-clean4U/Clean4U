package org.example.clean4u.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeApiController {
    private NoticeService noticeService;

    @PostMapping("/image")
    public String uploadImage(@RequestParam List<MultipartFile> files) {
        return "/uploads/notice/";
    }

}
