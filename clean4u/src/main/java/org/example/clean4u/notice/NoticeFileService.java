package org.example.clean4u.notice;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.errors.exception.Exception500;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeFileService {
    private final NoticeFileRepository noticeFileRepository;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "txt", "zip"
    );

    // 단일 파일 최대 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    public void validateFiles (List<MultipartFile> files) {
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
