package com.homestay.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CloudinaryService {
    Cloudinary cloudinary;
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public List<String> uploadFiles(List<MultipartFile> files) {
        List<CompletableFuture<String>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> uploadFile(file), executorService)
                        .handle((result, ex) -> {
                            if (ex != null) {
                                log.error("Error uploading file: {}", file.getOriginalFilename(), ex);
                                return null; // Hoặc có thể trả về một giá trị khác nếu cần
                            }
                            return result;
                        }))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .filter(url -> url != null) // Lọc bỏ các URL null do lỗi
                .collect(Collectors.toList());
    }

    public String uploadFile(MultipartFile file) {
        try {
            File convFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinary.uploader().upload(convFile, ObjectUtils.emptyMap());
            if (!convFile.delete()) {
                log.warn("Could not delete temporary file: {}", convFile.getAbsolutePath());
            }
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            log.error("Could not upload file: {}", file.getOriginalFilename(), e);
            return null; // Trả về null nếu có lỗi
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    public void deleteFiles(List<String> urls) {
        List<CompletableFuture<Void>> futures = urls.stream()
                .map(url -> CompletableFuture.runAsync(() -> {
                    String publicId = extractPublicId(url);
                    try {
                        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    } catch (Exception e) {
                        log.error("Error deleting file: {}", publicId, e);
                    }
                }, executorService))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private String extractPublicId(String url) {
        // Logic to safely extract publicId from URL
        return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
    }
}
