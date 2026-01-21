package org.example.clean4u.noticeFile;

import lombok.RequiredArgsConstructor;
import org.example.clean4u._core.errors.exception.Exception500;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class NoticeFileApiController {

    private final NoticeFileService noticeFileService;

    @GetMapping("/api/v1/files/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) throws IOException {
        NoticeFile file = noticeFileService.getFileInfo(fileId); // 파일 조회

//        Path path = noticeFileService.getFile(file); // 파일 경로

//        Resource resource = new PathResource(path); // Resource 변환
        Resource resource = noticeFileService.getFileResource(file); // Resource 변환

        // 헤더 생성
        HttpHeaders headers = noticeFileService.createDownloadHeaders(file, Paths.get(resource.getURI()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
