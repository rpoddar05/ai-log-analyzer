package com.rahul.ailoganalyzer.extractor;

import com.rahul.ailoganalyzer.config.LogAnalysisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RelevantLogExtractor {

    private final LogAnalysisProperties logAnalysisProperties;

    public RelevantLogExtractor(LogAnalysisProperties logAnalysisProperties) {
        this.logAnalysisProperties = logAnalysisProperties;
    }

    // Simple timestamp-like log line detector
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}.*"
    );

    public List<String> extractRelevantBlocks(String fileContent) {
        if (fileContent == null || fileContent.isBlank()) {
            log.warn("RelevantLogExtractor received null or blank file content");
            return List.of();
        }

        String[] lines = fileContent.split("\\R");
        List<String> extractedBlocks = new ArrayList<>();

        log.debug("Starting log extraction. Total lines in file: {}", lines.length);

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (isRelevantStart(line)) {
                StringBuilder block = new StringBuilder();

                int start = Math.max(0, i - logAnalysisProperties.getContextBefore());

                for (int j = start; j <= i; j++) {
                    block.append(lines[j]).append(System.lineSeparator());
                }

                int k = i + 1;
                while (k < lines.length) {
                    String nextLine = lines[k];

                    // Stop if a new timestamped log line starts and it is not stack trace continuation
                    if (looksLikeNewLogEntry(nextLine) && !isStackTraceLine(nextLine) && !isRelevantContinuation(nextLine)) {
                        break;
                    }

                    // If we hit another strong incident start, stop current block so it becomes its own incident
                    if (isRelevantStart(nextLine) && !isStackTraceLine(nextLine)) {
                        break;
                    }

                    block.append(nextLine).append(System.lineSeparator());
                    k++;
                }

                extractedBlocks.add(block.toString());
                i = k - 1;
            }
        }

        if (extractedBlocks.isEmpty()) {
            log.info("No relevant log sections found. Using fallback truncated raw content");
            String fallback = fileContent.length() > logAnalysisProperties.getMaxFallbackLogLength()
                    ? fileContent.substring(0, logAnalysisProperties.getMaxFallbackLogLength())
                    : fileContent;
            return List.of(fallback);
        }

        log.info("Relevant log extraction completed. Extracted {} incident block(s)", extractedBlocks.size());
        return extractedBlocks;
    }

    private boolean isRelevantStart(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }

        return line.contains("ERROR");
    }

    private boolean isRelevantContinuation(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }

        return line.startsWith("\tat")
                || line.trim().startsWith("at ")
                || line.startsWith("Caused by:")
                || line.startsWith("...")
                || line.contains("SQLTimeoutException")
                || line.contains("NullPointerException");
    }

    private boolean isStackTraceLine(String line) {
        if (line == null) {
            return false;
        }

        String trimmed = line.trim();
        return trimmed.startsWith("at ")
                || trimmed.startsWith("...")
                || trimmed.startsWith("Caused by:");
    }

    private boolean looksLikeNewLogEntry(String line) {
        if (line == null || line.isBlank()) {
            return false;
        }

        return TIMESTAMP_PATTERN.matcher(line).matches();
    }
}