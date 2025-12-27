package org.example.clean4u.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u.employee.Employee;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeResponse.DetailDTO saveNotice(NoticeRequest.@Valid SaveDTO dto, Employee sessionUser) {
        Notice notice = dto.toEntity(sessionUser);
        Notice savedNotice = noticeRepository.save(notice);

        return new NoticeResponse.DetailDTO(savedNotice);
    }

    public List<NoticeResponse.ListDTO> getAllNoticeList() {
        List<Notice> noticeList = noticeRepository.findAllByOrderByCreatedAtDesc();

        return noticeList.stream()
                .map(NoticeResponse.ListDTO::new)
                .collect(Collectors.toList());
    }

    public NoticeResponse.DetailDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        return new NoticeResponse.DetailDTO(notice);
    }

    public NoticeResponse.DetailDTO getFormForUpdate(Long noticeId, Employee sessionUser) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (sessionUser.isAdmin()) {
            return new NoticeResponse.DetailDTO(notice);
        }

        return new NoticeResponse.DetailDTO(notice);
    }

    @Transactional
    public Notice updateNotice(Long noticeId, NoticeRequest.@Valid UpdateDTO dto, Employee sessionUser) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!notice.getEmployee().getId().equals(sessionUser.getId())) {
            throw new Exception403("수정 권한이 없습니다.");
        }

        notice.update(dto);
        return notice;
    }

    @Transactional
    public void deleteNoticeById(Long noticeId, Long sessionUserId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!notice.getEmployee().getId().equals(sessionUserId)) {
            throw new Exception403("삭제 권한이 없습니다.");
        }

        noticeRepository.deleteById(noticeId);
    }

    public Long getNextNoticeId(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("공지사항 없음"));

        return noticeRepository
                .findNextId(notice.getCreatedAt(), noticeId)
                .orElse(null);
    }

    public Long getPrevNoticeId(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("공지사항 없음"));

        return noticeRepository
                .findPrevId(notice.getCreatedAt(), noticeId)
                .orElse(null);
    }
}
