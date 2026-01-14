package org.example.clean4u.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception403;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.errors.exception.Exception500;
import org.example.clean4u._core.response.PageResponse;
import org.example.clean4u._core.utils.FileUtil;
import org.example.clean4u.employee.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {
    @Value("${app.upload.notice-path}")
    private String noticePath;

    private final NoticeRepository noticeRepository;
    private final FileUtil fileUtil;

    @Transactional
    public Notice saveNotice(NoticeRequest.@Valid SaveDTO dto, Employee sessionUser) {
        List<String> noticeImageFileNames = new ArrayList<>();

        if (dto.getUploadImages() != null && !dto.getUploadImages().isEmpty()) {
            try {
                if (!fileUtil.isImageFiles(dto.getUploadImages())) {
                    throw new Exception400("이미지 파일만 업로드 가능합니다");
                }
                noticeImageFileNames.addAll(fileUtil.saveFiles(dto.getUploadImages(), noticePath));
            } catch (IOException e) {
                throw new Exception500("파일 저장 중 오류가 발생했습니다");
            }
        }
        Notice notice = dto.toEntity(sessionUser, noticeImageFileNames);
        noticeRepository.save(notice);

        return notice;
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
        Notice notice = noticeRepository.findByIdWithImages(noticeId)
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
        Notice notice = noticeRepository.findByIdWithImages(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }
        notice.update(dto); // 제목, 내용만

        if (dto.getUploadImages() != null && !dto.getUploadImages().isEmpty()) {
            if (!fileUtil.isImageFiles(dto.getUploadImages())) {
                throw new Exception400("이미지 파일만 업로드 가능합니다");
            }

            List<String> oldNoticeImages = new ArrayList<>(notice.getNoticeImages());

            try {
                List<String> newImageFilename = fileUtil.saveFiles(dto.getUploadImages(), noticePath);
                notice.clearImages();
                notice.addImages(newImageFilename);

                if (!oldNoticeImages.isEmpty()) {
                    fileUtil.deleteFiles(oldNoticeImages, noticePath);
                }

            } catch (IOException e) {
                throw new Exception500("파일 저장에 실패했습니다");
            }
        }
        return new NoticeResponse.DetailDTO(notice);
    }

    @Transactional
    public void deleteNoticeById(Long noticeId, Long sessionUserId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!notice.getEmployee().getId().equals(sessionUserId)) {
            throw new Exception403("삭제 권한이 없습니다.");
        }

        List<String> noticeImages = new ArrayList<>(notice.getNoticeImages());
        noticeRepository.deleteById(noticeId);
    }

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void deleteFilesAfterTransaction(List<String> filenames) {
//        try {
//            for (String filename : filenames) {
//                FileUtil.deleteFile(filename, NOTICE_IMAGES_DIR);
//            }
//        } catch (IOException e) {
//            log.error("파일 삭제 실패 (이미 DB는 삭제됨): {}", e.getMessage());
//        }
//    }

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

    @Transactional
    public void deleteNoticeImages(Long noticeId, Long sessionUserId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("공지사항이 없습니다"));

        if (!notice.getEmployee().getId().equals(sessionUserId)) {
            throw new Exception403("삭제 권한이 없습니다.");
        }

        List<String> noticeImages = notice.getNoticeImages();
        if (noticeImages != null && !noticeImages.isEmpty()) {
            try {
                for (String noticeImage : noticeImages) {
                    fileUtil.deleteFile(noticeImage, noticePath);
                }
            } catch (IOException e) {
                throw new Exception500("파일 삭제 중 오류가 발생했습니다");
            }
        }
        notice.clearImages();
    }
}
