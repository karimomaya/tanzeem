package com.tanzeem.image.service;

import com.tanzeem.image.dto.ImageUploadResponse;
import com.tanzeem.security.common.AuthContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8081}")  // Different port for image service
    private String baseUrl;

    private final List<String> allowedImageTypes = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "image/webp", "image/bmp", "image/svg+xml"
    );

    public ImageUploadResponse uploadImage(MultipartFile file, String context) {
        try {
            validateFile(file);

            String tenantId = AuthContextHolder.getTenantId();
            return saveImageFile(file.getInputStream(), file.getOriginalFilename(),
                    file.getContentType(), context, tenantId);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public ImageUploadResponse downloadAndStoreImage(String imageUrl, String context) {
        try {
            validateUrl(imageUrl);

            URL url = URI.create(imageUrl).toURL();
            URLConnection connection = url.openConnection();

            // Set headers
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            // Validate content
            String contentType = connection.getContentType();
            if (contentType != null) {
                contentType = contentType.split(";")[0].toLowerCase();
                if (!allowedImageTypes.contains(contentType)) {
                    throw new IllegalArgumentException("URL does not point to a valid image");
                }
            }

            long contentLength = connection.getContentLengthLong();
            if (contentLength > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("Image is too large (max 10MB)");
            }

            String filename = extractFilenameFromUrl(imageUrl);
            String tenantId = AuthContextHolder.getTenantId();

            try (InputStream inputStream = connection.getInputStream()) {
                return saveImageFile(inputStream, filename, contentType, context, tenantId);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to download image from URL: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<byte[]> getImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .body(imageBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    public void deleteImage(String filename) {
        try {

            String dir = uploadDir + "/"+ AuthContextHolder.getTenantId();
            Path filePath = Paths.get(dir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    private ImageUploadResponse saveImageFile(InputStream inputStream, String originalFilename,
                                              String contentType, String context, String tenantId) throws IOException {

        // Create directory structure: uploads/{tenantId}/{context}/

        Path uploadPath = Paths.get(uploadDir, tenantId != null ? tenantId : "default", context);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileExtension = getFileExtension(originalFilename, contentType);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        // Generate relative path for URL
        String relativePath = (tenantId != null ? tenantId : "default") + "/" + context + "/" + uniqueFilename;
        String imageUrl = baseUrl + "/api/images/" + relativePath;

        return new ImageUploadResponse(imageUrl, uniqueFilename, originalFilename, relativePath);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedImageTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("File must be a valid image type");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File is too large (max 10MB)");
        }
    }

    private void validateUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL is required");
        }

        URI uri = URI.create(imageUrl);
        if (!uri.isAbsolute() || (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    private String extractFilenameFromUrl(String url) {
        try {
            String path = new URI(url).getPath();
            String filename = path.substring(path.lastIndexOf('/') + 1);
            return filename.isEmpty() || !filename.contains(".") ? "downloaded-image" : filename;
        } catch (Exception e) {
            return "downloaded-image";
        }
    }

    private String getFileExtension(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf("."));
            if (ext.length() <= 5) {
                return ext;
            }
        }

        switch (contentType != null ? contentType.toLowerCase() : "") {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            case "image/bmp":
                return ".bmp";
            case "image/svg+xml":
                return ".svg";
            default:
                return ".jpg";
        }
    }
}
