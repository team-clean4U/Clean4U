package org.example.clean4u._core.utils;

import org.example.clean4u._core.errors.exception.Exception400;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUtil {
    public static final String IMAGES_DIR = "C:/uploads/";

    public static String saveFile(MultipartFile file) throws IOException {
        return saveFile(file, IMAGES_DIR);
    }

    public static String saveFile(MultipartFile file, String uploadDir) throws IOException {
        if(file == null || file.isEmpty()) {
            return null;
        }
        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IOException("파일명이 없습니다.");
        }

        String uuid = UUID.randomUUID().toString();
        String savedFileName = uuid + "_" + originalFilename;

        Path filePath = uploadPath.resolve(savedFileName);

        Files.copy(file.getInputStream(), filePath);

        return savedFileName;
    }

    public static List<String> saveFiles(List<MultipartFile> files, String uploadDir) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<String> savedNames = new ArrayList<>();
        for (MultipartFile file: files) {
            String saved = saveFile(file, uploadDir);
            if (saved != null) {
                savedNames.add(saved);
            }
        }
        return savedNames;
    }

     public static boolean isImageFile(MultipartFile file) {
        if(file == null || file.isEmpty()) {
            return false;
        }
         String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
     }

     public static boolean isImageFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return true;
        }
        for (MultipartFile file: files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return false;
            }
        }
        return true;
     }

    public static void deleteFile(String filename) throws IOException {
        deleteFile(filename, IMAGES_DIR);
    }

    public static void deleteFile(String filename, String uploadDir) throws IOException {
        if(filename == null || filename.isEmpty()) {
            return;
        }
        Path filePath = Paths.get(uploadDir, filename);
        if(Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
