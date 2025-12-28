package org.example.clean4u.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public PageResponse<NoticeResponse.ListDTO> getAllNoticeList(int page, int size) {
        int validPage = Math.max(0, page);
        int validSize = Math.max(1, Math.min(50, size));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(validPage, validSize, sort);

        Page<Notice> noticePage = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);

        return new PageResponse<>(noticePage, NoticeResponse.ListDTO::new);
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
    public NoticeResponse.DetailDTO updateNotice(Long noticeId, NoticeRequest.@Valid UpdateDTO dto, Employee sessionUser) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니댜다.");
        }

        notice.update(dto);
        return new NoticeResponse.DetailDTO(notice);
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
