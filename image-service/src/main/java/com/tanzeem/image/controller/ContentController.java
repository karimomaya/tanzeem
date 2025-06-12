package com.tanzeem.image.controller;


import com.tanzeem.image.dto.*;
import com.tanzeem.image.enums.ContentCategory;
import com.tanzeem.image.service.ContentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {
    private final ContentService contentService;


    // Single file upload - matches ProductModal's uploadContent call
    @PostMapping("/upload")
    public ResponseEntity<ContentUploadResponse> uploadContent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("context") String context,
            @RequestParam("entityType") String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary,
            @RequestParam(value = "order", defaultValue = "0") Integer order,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description) {

        ContentUploadResponse response = contentService.uploadContent(
                file, context, entityType, entityId, isPrimary, order, title, description);
        return ResponseEntity.ok(response);
    }

    // Multiple files upload - matches ProductModal's uploadMultipleContents call
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

    // Single URL download - matches ProductModal's downloadAndStoreContent call
    @PostMapping("/download-and-store")
    public ResponseEntity<ContentUploadResponse> downloadAndStoreContent(
            @RequestBody ContentUrlDownloadRequest request) {

        ContentUploadResponse response = contentService.downloadAndStoreContent(
                request.getContentUrl(),
                request.getContext(),
                request.getEntityType(),
                request.getEntityId(),
                request.getIsPrimary(),
                request.getOrder(),
                request.getTitle(),
                request.getDescription()
        );
        return ResponseEntity.ok(response);
    }

    // Multiple URLs download - matches ProductModal's downloadAndStoreMultipleContents call
    @PostMapping("/download-and-store-multiple")
    public ResponseEntity<List<ContentUploadResponse>> downloadAndStoreMultipleContents(
            @RequestBody ContentBulkDownloadRequest request) {

        List<ContentUploadResponse> responses = contentService.downloadAndStoreMultipleContents(
                request.getContentUrls(), request.getContext(),
                request.getEntityType(), request.getEntityId());
        return ResponseEntity.ok(responses);
    }

    // Get contents by entity - matches ProductModal's getContentsByEntity call
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<ContentResponse>> getContentsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        List<ContentResponse> contents = contentService.getContentsByEntity(entityType, entityId);
        return ResponseEntity.ok(contents);
    }

    // Get contents by entity and category - matches ProductModal's getContentsByEntityAndCategory call
    @GetMapping("/entity/{entityType}/{entityId}/category/{category}")
    public ResponseEntity<List<ContentResponse>> getContentsByEntityAndCategory(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @PathVariable String category) {

        ContentCategory contentCategory = ContentCategory.valueOf(category.toUpperCase());
        List<ContentResponse> contents = contentService.getContentsByEntityAndCategory(
                entityType, entityId, contentCategory);
        return ResponseEntity.ok(contents);
    }

    // Get primary content - matches ProductModal's getPrimaryContent call
    @GetMapping("/entity/{entityType}/{entityId}/primary")
    public ResponseEntity<ContentResponse> getPrimaryContent(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        Optional<ContentResponse> content = contentService.getPrimaryContent(entityType, entityId);
        return content.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Batch get contents for multiple entities
    @PostMapping("/entity/batch")
    public ResponseEntity<Map<Long, List<ContentResponse>>> getContentsForMultipleEntities(
            @RequestBody ContentBatchRequest request) {

        Map<Long, List<ContentResponse>> contents = contentService.getContentsForMultipleEntities(
                request.getEntityType(), request.getEntityIds());
        return ResponseEntity.ok(contents);
    }

    // Update entity references - matches ProductModal's updateContentEntityReferences call
    @PutMapping("/update-entity-references")
    public ResponseEntity<Void> updateContentEntityReferences(
            @RequestBody ContentEntityUpdateRequest request) {

        contentService.updateContentEntityReferences(
                request.getEntityType(),
                request.getEntityId(),
                request.getContentUrls()
        );
        return ResponseEntity.ok().build();
    }

    // Delete contents by entity
    @DeleteMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Void> deleteContentsByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        contentService.deleteContentsByEntity(entityType, entityId);
        return ResponseEntity.noContent().build();
    }

    // Serve content files - fixed path pattern
    @GetMapping("/**")
    public ResponseEntity<byte[]> getContent(HttpServletRequest request) {
        String filename = request.getRequestURI().substring("/api/contents/".length());
        return contentService.getContent(filename);
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = Map.of(
                "status", "UP",
                "service", "Content Service",
                "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(status);
    }
}
