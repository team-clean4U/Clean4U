package org.example.clean4u.notice;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception401;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u.employee.Employee;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeApiController {
    private final NoticeService noticeService;

    @PostMapping("/image")
    public String uploadImage(@RequestParam List<MultipartFile> files) {
        return "/uploads/notice/";
    }

    @PutMapping(value = "/{noticeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoticeResponse.DetailDTO> updateNotice (@PathVariable Long noticeId,
                                                                  @ModelAttribute NoticeRequest.UpdateDTO dto,
                                                                  HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            throw new Exception401("로그인이 필요합니다");
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다");
        }

        NoticeResponse.DetailDTO result = noticeService.updateNotice(noticeId, dto, sessionUser);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice (@PathVariable Long noticeId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        noticeService.deleteNoticeById(noticeId, sessionUser.getId());
        return ResponseEntity.noContent().build();
    }

}
