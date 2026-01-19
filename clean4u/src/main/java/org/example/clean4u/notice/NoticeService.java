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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {
    @Value("${app.upload.notice-path}")
    private String noticePath;

    @Value("${app.upload.notice-file-path}")
    private String noticeFilePath;

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final NoticeFileService noticeFileService;
    private final FileUtil fileUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Notice saveNotice(NoticeRequest.@Valid SaveDTO dto, Employee sessionUser) {
        Notice notice = dto.toEntity(sessionUser);

        List<MultipartFile> attachments = dto.getAttachments();
        attachFiles(attachments, notice);

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
        Notice notice = noticeRepository.findByIdWithFiles(noticeId)
                .orElseThrow(() -> new Exception404("해당 공지사항이 없습니다."));

        return new NoticeResponse.DetailDTO(notice);
    }

    public NoticeResponse.DetailDTO getFormForUpdate(Long noticeId, Employee sessionUser) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("해당 공지사항이 없습니다."));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        return new NoticeResponse.DetailDTO(notice);
    }

    @Transactional
    public NoticeResponse.DetailDTO updateNotice(Long noticeId, NoticeRequest.@Valid UpdateDTO dto, Employee sessionUser) {
        Notice notice = noticeRepository.findByIdWithFiles(noticeId)
                .orElseThrow(() -> new Exception404("해당 공지사항이 없습니다."));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }
        notice.update(dto); // 제목, 내용만

        if (!dto.getDeleteFileIds().isEmpty()) {
            List<NoticeFile> deleteTargets = noticeFileRepository.findAllById(dto.getDeleteFileIds());

            deleteFiles(deleteTargets);
            notice.removeFiles(deleteTargets);
        }

        updateNoticeFiles(notice.getId(), dto, sessionUser);
        return new NoticeResponse.DetailDTO(notice);
    }

    @Transactional
    public void updateNoticeFiles(Long noticeId, @Valid NoticeRequest.UpdateDTO dto, Employee sessionUser) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("해당 공지사항이 없습니다"));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            notice.getNoticeFiles().removeIf(file -> dto.getDeleteFileIds().contains(file.getId()));
        }

        attachFiles(dto.getNewAttachments(), notice);
    }

    @Transactional
    public void deleteNoticeById(Long noticeId, Employee sessionUser) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception400("해당 공지사항이 없습니다."));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        List<NoticeFile> noticeFiles = new ArrayList<>(notice.getNoticeFiles());
        noticeRepository.deleteById(noticeId);

        if (!noticeFiles.isEmpty()) {
            applicationEventPublisher.publishEvent(new NoticeFilesDeletedEvent(noticeFiles));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteFilesAfterTransaction(NoticeFilesDeletedEvent event) {
        try {
            for (NoticeFile file : event.getFiles()) {
                fileUtil.deleteFile(file.getStoredName(), noticePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패 (이미 DB는 삭제됨): {}", e.getMessage());
        }
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

    @Transactional
    public void deleteFiles(List<NoticeFile> files) {

        if (files == null || files.isEmpty()) { return; }

        for (NoticeFile file : files) {
            try {
                fileUtil.deleteFile(file.getStoredName(), noticeFilePath);
            } catch (IOException e) {
                log.error("첨부파일 삭제 실패: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public String uploadSummernoteImage(MultipartFile image) {

        if (image == null || image.isEmpty()) {
            throw new Exception400("업로드 할 이미지가 없습니다");
        }

        if (!fileUtil.isImageFile(image)) {
            throw new Exception400("이미지 파일만 업로드 가능합니다");
        }

        try {
            String filename = fileUtil.saveFile(image, noticePath);
            return "/images/notice/" + filename;
        } catch (IOException e) {
            throw new Exception500("파일 업로드에 실패했습니다");
        }
    }

    private void attachFiles(List<MultipartFile> files, Notice notice) {
        if (files == null || files.isEmpty()) return;

        noticeFileService.validateFiles(files);

        for (MultipartFile file: files) {
            try {
                String storedName = fileUtil.saveFile(file, noticeFilePath);

                NoticeFile noticeFile = NoticeFile.createNoticeFile(file, storedName, noticeFilePath);
                notice.addNoticeFile(noticeFile);
            } catch (IOException e) {
                throw new Exception500("첨부파일 저장에 실패했습니다");
            }
        }
    }

}
