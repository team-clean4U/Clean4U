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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeService {
    @Value("${app.upload.notice-path}")
    private String noticePath;

    @Value("${app.upload.notice-file-path}")
    private String noticeFilePath;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "txt", "zip"
    );

    // 단일 파일 최대 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final FileUtil fileUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public Notice saveNotice(NoticeRequest.@Valid SaveDTO dto, Employee sessionUser) {
        Notice notice = dto.toEntity(sessionUser);

        List<MultipartFile> attachments = dto.getAttachments();
        if (attachments != null || !attachments.isEmpty()) {

            validateFiles(dto.getAttachments());

            try {
                for (MultipartFile file : attachments) {
                    String originalName = file.getOriginalFilename();
                    String storedName = fileUtil.saveFile(file, noticeFilePath);

                    Path fullPath = Paths.get(noticeFilePath).resolve(storedName);

                    NoticeFile noticeFile = NoticeFile.builder()
                            .originalName(originalName)
                            .storedName(storedName)
                            .fileSize(file.getSize())
                            .filePath(fullPath.toString())
                            .build();


                    notice.addNoticeFile(noticeFile);
                }
            } catch (IOException e) {
                throw new Exception500("첨부파일 저장에 실패했습니다");
            }
        }

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
    public NoticeResponse.DetailDTO updateNoticeFiles(Long noticeId,
                                                      @Valid NoticeRequest.UpdateDTO dto,
                                                      Employee sessionUser) {

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new Exception404("해당 공지사항이 없습니다"));

        if (!sessionUser.isAdmin()) {
            throw new Exception403("공지사항 수정 권한이 없습니다.");
        }

        if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
            notice.getNoticeFiles().removeIf(file -> dto.getDeleteFileIds().contains(file.getId()));
        }

        if (dto.getNewAttachments() != null && !dto.getNewAttachments().isEmpty()) {
            validateFiles(dto.getNewAttachments());

            for (MultipartFile attachment : dto.getNewAttachments()) {
                try {
                    String originalName = attachment.getOriginalFilename();
                    String storedName = fileUtil.saveFile(attachment, noticeFilePath);

                    Path fullPath = Paths.get(noticeFilePath).resolve(storedName);

                    NoticeFile noticeFile = NoticeFile.builder()
                            .originalName(originalName)
                            .storedName(storedName)
                            .fileSize(attachment.getSize())
                            .filePath(fullPath.toString())
                            .build();

                    notice.addNoticeFile(noticeFile);

                } catch (IOException e) {
                    throw new Exception500("첨부파일 저장에 실패했습니다");
                }
            }
        }

        return new NoticeResponse.DetailDTO(notice);
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

    // 파일
    private void validateFiles (List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                throw new Exception400("업로드할 파일이 없습니다");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new Exception400("최대 용량을 초과하였습니다, 최대 10MB까지 업로드 가능합니다");
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.contains(".")) {
                throw new Exception400("확장자가 없는 파일입니다");
            }

            String extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new Exception400("허용되지 않는 파일 형식입니다" + extension);
            }
        }
    }

    public NoticeFile getFileInfo(Long fileId) {
        NoticeFile info = noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new Exception404("해당 파일이 없습니다"));

        return info;
    }

    public Path getFile(NoticeFile file) {
        String filePath = file.getFilePath();
        if (filePath == null || filePath.isEmpty()) {
            throw new Exception500("파일 경로가 없습니다");
        }

        Path path = Paths.get(filePath);

        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new Exception500("서버 내부 오류가 발생했습니다");
        }

        return path;
    }

    public HttpHeaders createDownloadHeaders(NoticeFile file, Path path) {
        HttpHeaders headers = new HttpHeaders();

        String contentType = file.getContentType();
        if (contentType == null) {
            try {
                contentType = Files.probeContentType(path);
            } catch (Exception ignored) {}
        }
        headers.setContentType(MediaType.parseMediaType(
                contentType != null ? contentType : "application/octet-stream"
        ));

        String encodedName = URLEncoder.encode(file.getOriginalName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedName
        );

        headers.setContentLength(file.getFileSize());

        return headers;
    }

}
