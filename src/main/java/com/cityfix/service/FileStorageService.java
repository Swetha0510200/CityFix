package com.cityfix.service;

import com.cityfix.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${cityfix.upload.dir:./uploads}")
    private String uploadDirStr;

    private Path uploadPath;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    @PostConstruct
    public void init() {
        try {
            this.uploadPath = Paths.get(uploadDirStr).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder: " + uploadDirStr, e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BadRequestException("Invalid uploaded file name.");
        }

        // Validate extension
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid image format (" + extension + "). Allowed formats: JPG, JPEG, PNG, WEBP.");
        }

        // Validate MIME type
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new BadRequestException("Uploaded file is not a valid image.");
        }

        // Generate unique, safe filename
        String cleanFileName = "img_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;

        try {
            Path targetLocation = this.uploadPath.resolve(cleanFileName).normalize();
            // Prevent path traversal
            if (!targetLocation.startsWith(this.uploadPath)) {
                throw new BadRequestException("Cannot store file outside current upload directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return cleanFileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + cleanFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.uploadPath.resolve(fileName).normalize();
            if (!filePath.startsWith(this.uploadPath)) {
                return null;
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (MalformedURLException ex) {
            return null;
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) return;
        try {
            Path filePath = this.uploadPath.resolve(fileName).normalize();
            if (filePath.startsWith(this.uploadPath)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException ignored) {
        }
    }

    public Path getUploadPath() {
        return uploadPath;
    }
}
