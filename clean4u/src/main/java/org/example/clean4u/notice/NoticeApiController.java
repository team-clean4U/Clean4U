package org.example.clean4u.notice;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.response.ApiResponse;
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

    @PutMapping(value = "/{noticeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<NoticeResponse.DetailDTO>> updateNotice (@PathVariable Long noticeId,
                                                                              @ModelAttribute NoticeRequest.UpdateDTO dto,
                                                                              HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        NoticeResponse.DetailDTO result = noticeService.updateNotice(noticeId, dto, sessionUser);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice (@PathVariable Long noticeId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        noticeService.deleteNoticeById(noticeId, sessionUser);
        return ResponseEntity.ok(ApiResponse.ok("삭제가 완료되었습니다"));
    }

    @DeleteMapping("{noticeId}/image")
    public ResponseEntity<ApiResponse<Void>> deleteNoticeImages(@PathVariable Long noticeId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        noticeService.deleteNoticeImages(noticeId, sessionUser);
        return ResponseEntity.ok(ApiResponse.ok("이미지 삭제가 완료되었습니다"));
    }

}
