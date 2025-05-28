package com.tanzeem.image.controller;


import com.tanzeem.image.dto.ImageUploadResponse;
import com.tanzeem.image.dto.ImageUrlRequest;
import com.tanzeem.image.service.ImageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageUploadResponse> uploadImage(
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "context", defaultValue = "general") String context) {
        return ResponseEntity.ok(imageService.uploadImage(file, context));
    }

    @PostMapping("/upload-from-url")
    public ResponseEntity<ImageUploadResponse> uploadFromUrl(@RequestBody ImageUrlRequest request) {
        return ResponseEntity.ok(imageService.downloadAndStoreImage(request.getImageUrl(), request.getContext()));
    }

    @GetMapping("/**")
    public ResponseEntity<byte[]> getImage(HttpServletRequest request) {
        String filename = request.getRequestURI().substring("/api/images/".length());
        return imageService.getImage(filename);
    }

    @DeleteMapping("/**")
    public ResponseEntity<Void> deleteImage(HttpServletRequest request) {
        String filename = request.getRequestURI().substring("/api/images/".length());
        imageService.deleteImage(filename);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Image service is running");
    }
}
