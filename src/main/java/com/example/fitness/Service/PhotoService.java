package com.example.fitness.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PhotoService {
    @Value("${upload.dir}")
    private String uploadDir;

    public String savePhoto(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст");
        }

        String directoryPath = uploadDir + "/" + subDir;
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(directoryPath, fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/images/" + subDir + "/" + fileName;
    }
}