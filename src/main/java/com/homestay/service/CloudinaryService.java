package com.homestay.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {
    Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            File convFile = convertMultiPartToFile(file);
            Map uploadResult = cloudinary.uploader().upload(convFile, ObjectUtils.emptyMap());
            if (!convFile.delete()) { // Giải phóng bộ nhớ
                throw new RuntimeException("File does not exist");
            }
            return uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not upload file due to error: " + e.getMessage());
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
