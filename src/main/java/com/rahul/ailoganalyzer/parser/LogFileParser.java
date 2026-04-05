package com.rahul.ailoganalyzer.parser;

import com.rahul.ailoganalyzer.config.LogAnalysisProperties;
import com.rahul.ailoganalyzer.exception.AiAnalysisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LogFileParser {

    private final LogAnalysisProperties logAnalysisProperties;

    public LogFileParser(LogAnalysisProperties logAnalysisProperties) {
        this.logAnalysisProperties = logAnalysisProperties;
    }

    public String parse(MultipartFile file) {
        validateFile(file);

        String fileName = file.getOriginalFilename();
        log.info("Parsing uploaded log file: {}", fileName);
        log.debug("Uploaded file size: {} bytes", file.getSize());

        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.debug("Parsed file successfully. Content length: {}", fileContent.length());
            return fileContent;
        } catch (IOException e) {
            log.error("Failed to read uploaded file: {}", fileName, e);
            throw new AiAnalysisException("Failed to process uploaded log file");
        }

    }


    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is null or empty");
            throw new AiAnalysisException("Uploaded file is empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            log.warn("Uploaded file name is missing");
            throw new AiAnalysisException("Uploaded file must have a valid name");
        }

        if (!fileName.endsWith(".log") && !fileName.endsWith(".txt")) {
            log.warn("Unsupported uploaded file type: {}", fileName);
            throw new AiAnalysisException("Only .log or .txt files are allowed");
        }

        if (file.getSize() > logAnalysisProperties.getMaxFileSizeBytes()) {
            log.warn("Uploaded file exceeds max size. File: {}, Size: {}", fileName, file.getSize());
            throw new AiAnalysisException("Uploaded file exceeds maximum allowed size of 1 MB");
        }

        log.info("Uploaded file validation passed for file: {}", fileName);
    }
}
