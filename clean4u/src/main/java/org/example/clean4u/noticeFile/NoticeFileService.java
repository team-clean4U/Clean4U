package org.example.clean4u.noticeFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.clean4u._core.errors.exception.Exception400;
import org.example.clean4u._core.errors.exception.Exception404;
import org.example.clean4u._core.errors.exception.Exception500;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
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
@Slf4j
public class NoticeFileService {
    private final NoticeFileRepository noticeFileRepository;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "pdf", "txt", "zip"
    );

    // 단일 파일 최대 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    public void validateFiles (List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {

            if (file != null && !file.isEmpty()) {
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
    }

    public Resource getFileResource(NoticeFile file) {
        String dbPath = file.getFilePath();
        if (dbPath.startsWith("/")) {
            dbPath = dbPath.substring(1);
        }

        String fileName = Paths.get(dbPath).getFileName().toString();
        Path externalPath = Paths.get("C:/uploads/files").resolve(fileName);
        Resource resource = new FileSystemResource(externalPath);

        if (!resource.exists()) {
            Path internalPath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static", dbPath);
            resource = new FileSystemResource(internalPath);
        }

        if (!resource.exists()) {
            resource = new ClassPathResource("static/" + dbPath);
        }

        if (!resource.exists()) {
            throw new Exception500("파일을 찾을 수 없습니다: " + dbPath);
        }
        return resource;
    }

    public NoticeFile getFileInfo(Long fileId) {
        NoticeFile info = noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new Exception404("해당 파일이 없습니다"));

        return info;
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
