package com.tanzeem.image.service;

import com.tanzeem.image.dto.ContentResponse;
import com.tanzeem.image.dto.ContentUploadResponse;
import com.tanzeem.image.dto.ContentUrlRequest;
import com.tanzeem.image.entity.Content;
import com.tanzeem.image.enums.ContentCategory;
import com.tanzeem.image.repository.ContentRepository;
import com.tanzeem.security.common.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentService {
    private final ContentRepository contentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.base.url:http://localhost:8081}")
    private String baseUrl;

    @Value("${app.max.file.size:50MB}")
    private String maxFileSize;

    // Define allowed file types and their categories
    private final Map<String, ContentCategory> allowedContentTypes;

    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
        this.allowedContentTypes = buildAllowedContentTypes();
    }

    private Map<String, ContentCategory> buildAllowedContentTypes() {
        Map<String, ContentCategory> types = new HashMap<>();

        // Images
        types.put("image/jpeg", ContentCategory.IMAGE);
        types.put("image/jpg", ContentCategory.IMAGE);
        types.put("image/png", ContentCategory.IMAGE);
        types.put("image/gif", ContentCategory.IMAGE);
        types.put("image/webp", ContentCategory.IMAGE);
        types.put("image/bmp", ContentCategory.IMAGE);
        types.put("image/svg+xml", ContentCategory.IMAGE);

        // Documents
        types.put("application/pdf", ContentCategory.DOCUMENT);
        types.put("application/msword", ContentCategory.DOCUMENT);
        types.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ContentCategory.DOCUMENT);
        types.put("application/vnd.ms-excel", ContentCategory.DOCUMENT);
        types.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ContentCategory.DOCUMENT);
        types.put("application/vnd.ms-powerpoint", ContentCategory.DOCUMENT);
        types.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", ContentCategory.DOCUMENT);

        // Text
        types.put("text/plain", ContentCategory.TEXT);
        types.put("text/csv", ContentCategory.TEXT);
        types.put("application/json", ContentCategory.TEXT);
        types.put("text/xml", ContentCategory.TEXT);
        types.put("text/html", ContentCategory.TEXT);
        types.put("text/css", ContentCategory.TEXT);
        types.put("text/javascript", ContentCategory.TEXT);

        // Videos
        types.put("video/mp4", ContentCategory.VIDEO);
        types.put("video/avi", ContentCategory.VIDEO);
        types.put("video/quicktime", ContentCategory.VIDEO);
        types.put("video/x-msvideo", ContentCategory.VIDEO);
        types.put("video/webm", ContentCategory.VIDEO);
        types.put("video/x-matroska", ContentCategory.VIDEO);

        // Audio
        types.put("audio/mpeg", ContentCategory.AUDIO);
        types.put("audio/wav", ContentCategory.AUDIO);
        types.put("audio/ogg", ContentCategory.AUDIO);
        types.put("audio/aac", ContentCategory.AUDIO);
        types.put("audio/mp4", ContentCategory.AUDIO);

        // Archives
        types.put("application/zip", ContentCategory.ARCHIVE);
        types.put("application/x-rar-compressed", ContentCategory.ARCHIVE);
        types.put("application/x-7z-compressed", ContentCategory.ARCHIVE);

        return Collections.unmodifiableMap(types);
    }


    public void updateContentEntityReferences(String entityType, Long entityId, List<String> contentUrls) {
        // Implementation to update existing content entities with new entity references
        // This is useful when creating a new entity and need to link uploaded content
        String tenantId = AuthContextHolder.getTenantId();

        for (String contentUrl : contentUrls) {
            // Find content by URL and update entity references
            // This is a simplified implementation - you might need more sophisticated logic
            List<Content> contents = contentRepository.findByTenantIdAndUrl(tenantId, contentUrl);
            contents.forEach(content -> {
                content.setEntityType(entityType);
                content.setEntityId(entityId);
            });
            contentRepository.saveAll(contents);
        }
    }

    @Transactional
    public ContentUploadResponse uploadContent(MultipartFile file, String context,
                                               String entityType, Long entityId,
                                               Boolean isPrimary, Integer order,
                                               String title, String description) {
        try {
            validateFile(file);
            String tenantId = AuthContextHolder.getTenantId();

            // Check for duplicates
            String fileHash = calculateFileHash(file.getInputStream());
            Optional<Content> existingContent = contentRepository.findByTenantIdAndFileHash(tenantId, fileHash);
            if (existingContent.isPresent()) {
                // Return existing content info instead of uploading duplicate
                return mapToUploadResponse(existingContent.get());
            }

            ContentUploadResponse response = saveContentFile(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    context,
                    tenantId
            );

            ContentCategory contentCategory = ContentCategory.fromMimeType(file.getContentType());

            // Save to database
            Content contentEntity = Content.builder()
                    .filename(response.getFilename())
                    .originalName(response.getOriginalName())
                    .url(response.getUrl())
                    .relativePath(response.getRelativePath())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .context(context)
                    .entityType(entityType)
                    .entityId(entityId)
                    .contentOrder(order)
                    .isPrimary(isPrimary != null ? isPrimary : false)
                    .contentCategory(contentCategory)
                    .title(title)
                    .description(description)
                    .fileHash(fileHash)
                    .isPublic(false)
                    .downloadCount(0L)
                    .tenantId(tenantId)
                    .build();

            contentRepository.save(contentEntity);
            response.setContentId(contentEntity.getId());
            response.setContentCategory(contentCategory.name());

            return response;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Transactional
    public List<ContentUploadResponse> uploadMultipleContents(List<MultipartFile> files,
                                                              String context, String entityType,
                                                              Long entityId) {
        List<ContentUploadResponse> responses = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            boolean isPrimary = i == 0; // First file is primary
            ContentUploadResponse response = uploadContent(file, context, entityType, entityId,
                    isPrimary, i, null, null);
            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public ContentUploadResponse downloadAndStoreContent(String contentUrl, String context,
                                                         String entityType, Long entityId,
                                                         Boolean isPrimary, Integer order,
                                                         String title, String description) {
        try {
            validateUrl(contentUrl);

            URL url = URI.create(contentUrl).toURL();
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            String contentType = connection.getContentType();
            if (contentType != null) {
                contentType = contentType.split(";")[0].toLowerCase();
                if (!allowedContentTypes.containsKey(contentType)) {
                    throw new IllegalArgumentException("URL does not point to a supported file type");
                }
            }

            long contentLength = connection.getContentLengthLong();
            long maxSizeBytes = parseMaxFileSize();
            if (contentLength > maxSizeBytes) {
                throw new IllegalArgumentException("File is too large (max " + maxFileSize + ")");
            }

            String filename = extractFilenameFromUrl(contentUrl);
            String tenantId = AuthContextHolder.getTenantId();

            ContentUploadResponse response;
            String fileHash;
            try (InputStream inputStream = connection.getInputStream()) {
                fileHash = calculateFileHash(inputStream);

                // Check for duplicates
                Optional<Content> existingContent = contentRepository.findByTenantIdAndFileHash(tenantId, fileHash);
                if (existingContent.isPresent()) {
                    return mapToUploadResponse(existingContent.get());
                }

                // Reset stream for actual download
                try (InputStream downloadStream = url.openStream()) {
                    response = saveContentFile(downloadStream, filename, contentType, context, tenantId);
                }
            }

            ContentCategory contentCategory = ContentCategory.fromMimeType(contentType);

            // Save to database
            Content contentEntity = Content.builder()
                    .filename(response.getFilename())
                    .originalName(response.getOriginalName())
                    .url(response.getUrl())
                    .relativePath(response.getRelativePath())
                    .contentType(contentType)
                    .size(contentLength > 0 ? contentLength : 0L)
                    .context(context)
                    .entityType(entityType)
                    .entityId(entityId)
                    .contentOrder(order)
                    .isPrimary(isPrimary != null ? isPrimary : false)
                    .contentCategory(contentCategory)
                    .title(title)
                    .description(description)
                    .fileHash(fileHash)
                    .isPublic(false)
                    .downloadCount(0L)
                    .tenantId(tenantId)
                    .build();

            contentRepository.save(contentEntity);
            response.setContentId(contentEntity.getId());
            response.setContentCategory(contentCategory.name());

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to download content from URL: " + e.getMessage(), e);
        }
    }

    public List<ContentResponse> getContentsByEntity(String entityType, Long entityId) {
        String tenantId = AuthContextHolder.getTenantId();
        List<Content> contents = contentRepository.findByTenantIdAndEntityTypeAndEntityIdOrderByContentOrderAsc(
                tenantId, entityType, entityId);

        return contents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ContentResponse> getContentsByEntityAndCategory(String entityType, Long entityId, ContentCategory category) {
        String tenantId = AuthContextHolder.getTenantId();
        List<Content> contents = contentRepository.findByTenantIdAndEntityTypeAndEntityIdAndContentCategoryOrderByContentOrderAsc(
                tenantId, entityType, entityId, category);

        return contents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ContentResponse> getPrimaryContent(String entityType, Long entityId) {
        String tenantId = AuthContextHolder.getTenantId();
        Optional<Content> content = contentRepository.findByTenantIdAndEntityTypeAndEntityIdAndIsPrimary(
                tenantId, entityType, entityId, true);

        return content.map(this::mapToResponse);
    }

    public Map<Long, List<ContentResponse>> getContentsForMultipleEntities(String entityType, List<Long> entityIds) {
        String tenantId = AuthContextHolder.getTenantId();
        List<Content> contents = contentRepository.findByTenantIdAndEntityTypeAndEntityIdIn(
                tenantId, entityType, entityIds);

        return contents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.groupingBy(ContentResponse::getEntityId));
    }

    public Map<Long, List<ContentResponse>> getContentsForMultipleEntitiesByCategory(
            String entityType, List<Long> entityIds, ContentCategory category) {
        String tenantId = AuthContextHolder.getTenantId();
        List<Content> contents = contentRepository.findByTenantIdAndEntityTypeAndEntityIdInAndContentCategory(
                tenantId, entityType, entityIds, category);

        return contents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.groupingBy(ContentResponse::getEntityId));
    }

    @Transactional
    public void incrementDownloadCount(Long contentId) {
        contentRepository.incrementDownloadCount(contentId);
    }

    public ResponseEntity<byte[]> getContent(String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] contentBytes = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(contentBytes);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Transactional
    public void deleteContentsByEntity(String entityType, Long entityId) {
        String tenantId = AuthContextHolder.getTenantId();
        List<Content> contents = contentRepository.findByTenantIdAndEntityTypeAndEntityIdOrderByContentOrderAsc(
                tenantId, entityType, entityId);

        // Delete physical files
        for (Content content : contents) {
            try {
                Path filePath = Paths.get(uploadDir, content.getRelativePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but continue
            }
        }

        // Delete database records
        contentRepository.deleteByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    // Helper methods
    private ContentUploadResponse saveContentFile(InputStream inputStream, String originalFilename,
                                                  String contentType, String context, String tenantId) throws IOException {

        Path uploadPath = Paths.get(uploadDir, tenantId != null ? tenantId : "default", context);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileExtension = getFileExtension(originalFilename, contentType);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = (tenantId != null ? tenantId : "default") + "/" + context + "/" + uniqueFilename;
        String contentUrl = baseUrl + "/api/contents/" + relativePath;

        return ContentUploadResponse.builder()
                .url(contentUrl)
                .filename(uniqueFilename)
                .originalName(originalFilename)
                .relativePath(relativePath)
                .build();
    }

    @Transactional
    public List<ContentUploadResponse> downloadAndStoreMultipleContents(List<ContentUrlRequest> urlRequests,
                                                                        String context, String entityType,
                                                                        Long entityId) {
        List<ContentUploadResponse> responses = new ArrayList<>();

        for (int i = 0; i < urlRequests.size(); i++) {
            ContentUrlRequest urlRequest = urlRequests.get(i);
            boolean isPrimary = i == 0; // First content is primary

            try {
                ContentUploadResponse response = downloadAndStoreContent(
                        urlRequest.getUrl(), context, entityType, entityId,
                        isPrimary, urlRequest.getOrder(), null, null
                );
                responses.add(response);
            } catch (Exception e) {
                // Log error but continue with other URLs
                throw new RuntimeException("Failed to download content: " + urlRequest.getUrl(), e);
            }
        }

        return responses;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedContentTypes.containsKey(contentType.toLowerCase())) {
            throw new IllegalArgumentException("File type not supported: " + contentType);
        }

        long maxSizeBytes = parseMaxFileSize();
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException("File is too large (max " + maxFileSize + ")");
        }
    }

    private void validateUrl(String contentUrl) {
        if (contentUrl == null || contentUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Content URL is required");
        }

        URI uri = URI.create(contentUrl);
        if (!uri.isAbsolute() || (!uri.getScheme().equals("http") && !uri.getScheme().equals("https"))) {
            throw new IllegalArgumentException("Invalid URL format");
        }
    }

    private String calculateFileHash(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate file hash", e);
        }
    }

    private long parseMaxFileSize() {
        String size = maxFileSize.toLowerCase();
        long multiplier = 1;

        if (size.endsWith("kb")) {
            multiplier = 1024;
            size = size.substring(0, size.length() - 2);
        } else if (size.endsWith("mb")) {
            multiplier = 1024 * 1024;
            size = size.substring(0, size.length() - 2);
        } else if (size.endsWith("gb")) {
            multiplier = 1024 * 1024 * 1024;
            size = size.substring(0, size.length() - 2);
        }

        try {
            return Long.parseLong(size.trim()) * multiplier;
        } catch (NumberFormatException e) {
            return 50 * 1024 * 1024; // Default 50MB
        }
    }

    private String extractFilenameFromUrl(String url) {
        try {
            String path = new URI(url).getPath();
            String filename = path.substring(path.lastIndexOf('/') + 1);
            return filename.isEmpty() || !filename.contains(".") ? "downloaded-file" : filename;
        } catch (Exception e) {
            return "downloaded-file";
        }
    }

    private String getFileExtension(String filename, String contentType) {
        if (filename != null && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf("."));
            if (ext.length() <= 10) { // Reasonable extension length
                return ext;
            }
        }

        // Fallback based on content type
        return getExtensionFromContentType(contentType);
    }

    private String getExtensionFromContentType(String contentType) {
        if (contentType == null) return ".bin";

        Map<String, String> extensionMap = Map.of(
                "image/jpeg", ".jpg",
                "image/png", ".png",
                "image/gif", ".gif",
                "application/pdf", ".pdf",
                "application/msword", ".doc",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx",
                "application/vnd.ms-excel", ".xls",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx",
                "video/mp4", ".mp4",
                "audio/mpeg", ".mp3"
        );

        return extensionMap.getOrDefault(contentType.toLowerCase(), ".bin");
    }

    private ContentResponse mapToResponse(Content content) {
        return ContentResponse.builder()
                .id(content.getId())
                .filename(content.getFilename())
                .originalName(content.getOriginalName())
                .url(content.getUrl())
                .contentType(content.getContentType())
                .size(content.getSize())
                .context(content.getContext())
                .entityType(content.getEntityType())
                .entityId(content.getEntityId())
                .contentOrder(content.getContentOrder())
                .isPrimary(content.getIsPrimary())
                .contentCategory(content.getContentCategory())
                .title(content.getTitle())
                .description(content.getDescription())
                .alt(content.getAlt())
                .isPublic(content.getIsPublic())
                .downloadCount(content.getDownloadCount())
                .fileHash(content.getFileHash())
                .createdAt(content.getCreatedAt())
                .build();
    }

    private ContentUploadResponse mapToUploadResponse(Content content) {
        ContentUploadResponse response = new ContentUploadResponse(
                content.getUrl(),
                content.getFilename(),
                content.getOriginalName(),
                content.getRelativePath(),
                content.getId(),
                content.getContentCategory().name()
        );
        return response;
    }
}
