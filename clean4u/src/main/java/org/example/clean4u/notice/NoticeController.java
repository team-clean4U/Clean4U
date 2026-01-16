package org.example.clean4u.notice;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지 생성 화면
    @GetMapping("/notices/new")
    public String saveForm(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        model.addAttribute("writer", sessionUser.getName());
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css",
                "/css/notice.css", "/css/notice-summernote.css"));

        return "/notice/save-form";
    }
    // 공지 생성 post
    @PostMapping("/notices/new")
    public String saveProc(@Valid NoticeRequest.SaveDTO dto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        Notice notice = noticeService.saveNotice(dto, sessionUser);

        return "redirect:/notices/" + notice.getId();
    }

    // 공지 목록
    @GetMapping("/notices")
    public String noticeList(Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "5") int size
    ) {

        int pageIndex = Math.max(0, page - 1);

        PageResponse<NoticeResponse.ListDTO> noticeListPage = noticeService.getAllNoticeList(pageIndex, size);
        model.addAttribute("noticePage", noticeListPage);
        model.addAttribute("additionalCss", Arrays.asList("/css/pageLink.css", "/css/notice.css"));

        return "/notice/list-form";
    }

    // 공지 디테일
    @GetMapping("/notices/{noticeId}")
    public String detail(@PathVariable Long noticeId, Model model, HttpSession session) {
        NoticeResponse.DetailDTO notice = noticeService.getNoticeById(noticeId);
        Long nextId = noticeService.getNextNoticeId(noticeId);
        Long prevId = noticeService.getPrevNoticeId(noticeId);

        model.addAttribute("notice", notice);
        model.addAttribute("nextId", nextId);
        model.addAttribute("prevId", prevId);
        model.addAttribute("hasPrev", prevId != null);
        model.addAttribute("hasNext", nextId != null);

        Employee employee = (Employee) session.getAttribute("sessionUser");

        boolean isAdmin = employee.isAdmin();
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("additionalCss", Arrays.asList("/css/detail.css", "/css/notice.css"));

        return "/notice/detail-form";
    }

    // 공지 수정 화면
    @GetMapping("/notices/{noticeId}/edit")
    public String updateForm(@PathVariable Long noticeId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        NoticeResponse.DetailDTO notice = noticeService.getFormForUpdate(noticeId, sessionUser);
        model.addAttribute("notice", notice);
        model.addAttribute("additionalCss", Arrays.asList("/css/update.css",
                "/css/notice.css", "/css/notice-summernote.css"));

        return "/notice/update-form";
    }

    @PostMapping("/notices/{noticeId}/image")
    public String updateNoticeImages(@PathVariable Long noticeId,
                                     @ModelAttribute @Valid NoticeRequest.ImageUploadDTO dto,
                                     HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");
        noticeService.updateNoticeImages(noticeId, dto, sessionUser);

        return "redirect:/notices/" + noticeId;
    }
}
