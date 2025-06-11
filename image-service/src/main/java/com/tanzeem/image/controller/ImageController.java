package com.tanzeem.image.controller;


import com.tanzeem.image.dto.ContentResponse;
import com.tanzeem.image.dto.ContentUploadResponse;
import com.tanzeem.image.dto.ImageUrlRequest;
import com.tanzeem.image.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ContentService contentService;

    public ImageController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<List<ContentUploadResponse>> uploadMultipleContents(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("context") String context,
            @RequestParam("entityType") String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId) {

        List<ContentUploadResponse> responses = contentService.uploadMultipleContents(
                files, context, entityType, entityId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/download-and-store-multiple")
    public ResponseEntity<List<ContentUploadResponse>> downloadAndStoreMultipleContents(
            @RequestBody ContentBulkDownloadRequest request) {

        List<ContentUploadResponse> responses = contentService.downloadAndStoreMultipleContents(
                request.getContentUrls(), request.getContext(),
                request.getEntityType(), request.getEntityId());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/entity/batch")
    public ResponseEntity<Map<Long, List<ContentResponse>>> getContentsForMultipleEntities(
            @RequestBody ContentBatchRequest request) {

        Map<Long, List<ContentResponse>> contents = contentService.getContentsForMultipleEntities(
                request.getEntityType(), request.getEntityIds());
        return ResponseEntity.ok(contents);
    }
    @PostMapping("/upload")
    public ResponseEntity<ContentUploadResponse> uploadImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "context", defaultValue = "general") String context) {
        return ResponseEntity.ok(contentService.uploadImage(file, context));
    }

    @PostMapping("/upload-from-url")
    public ResponseEntity<ContentUploadResponse> uploadFromUrl(@RequestBody ImageUrlRequest request) {
        return ResponseEntity.ok(contentService.downloadAndStoreImage(request.getImageUrl(), request.getContext()));
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) {
        String filename = request.getRequestURI().substring("/api/images/".length());
        return contentService.getImage(filename);
    }

    @DeleteMapping("/**")
    public ResponseEntity<Void> deleteImage(HttpServletRequest request) {
        String filename = request.getRequestURI().substring("/api/images/".length());
        contentService.deleteImage(filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image service is running");
    }
}
