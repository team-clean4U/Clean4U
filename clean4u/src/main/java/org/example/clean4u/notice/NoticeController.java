package org.example.clean4u.notice;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u.employee.Employee;
import org.example.clean4u.employee.UserRole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    // 공지 생성 화면
    @GetMapping("/notice/save")
    public String saveForm(Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (sessionUser.getUserRole() != UserRole.관리자) {
            throw new Exception403("공지사항 작성 권한이 없습니다.");
        }

        model.addAttribute("writer", sessionUser.getName());

        return "/notice/save-form";
    }
    // 공지 생성 post
    @PostMapping("/notice/save")
    public String saveProc(@Valid NoticeRequest.SaveDTO dto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 작성 권한이 없습니다.");
        }

        NoticeResponse.DetailDTO notice = noticeService.saveNotice(dto, sessionUser);

        return "redirect:/notice/" + notice.getId();
    }
    // 공지 목록
    @GetMapping("/notice/list")
    public String noticeList(Model model) {
        List<NoticeResponse.ListDTO> noticeList = noticeService.getAllNoticeList();
        model.addAttribute("noticeList", noticeList);
        return "/notice/list-form";
    }

    // 공지 디테일
    @GetMapping("/notice/{noticeId}")
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

        return "/notice/detail-form";
    }

    // 공지 수정 화면
    @GetMapping("/notice/{noticeId}/update")
    public String updateForm(@PathVariable Long noticeId, Model model, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        NoticeResponse.DetailDTO notice = noticeService.getFormForUpdate(noticeId, sessionUser);
        model.addAttribute("notice", notice);

        return "/notice/update-form";
    }
    // 공지 수정 post
    @PostMapping("/notice/{noticeId}/update")
    public String updateProc(@PathVariable Long noticeId, @Valid NoticeRequest.UpdateDTO dto, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        noticeService.updateNotice(noticeId, dto, sessionUser);
        return "redirect:/notice/" + noticeId;
    }

    @PostMapping("/notice/{noticeId}/delete")
    public String deleteNotice(@PathVariable Long noticeId, HttpSession session) {
        Employee sessionUser = (Employee) session.getAttribute("sessionUser");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 삭제 권한이 없습니다.");
        }

        noticeService.deleteNoticeById(noticeId, sessionUser.getId());
        return "redirect:/notice/list";
    }

}
