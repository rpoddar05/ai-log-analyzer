package com.rahul.ailoganalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.ailoganalyzer.config.LogAnalysisProperties;
import com.rahul.ailoganalyzer.dto.LogAnalysisRequest;
import com.rahul.ailoganalyzer.dto.LogAnalysisResponse;
import com.rahul.ailoganalyzer.dto.LogFileAnalysisResponse;
import com.rahul.ailoganalyzer.exception.AiAnalysisException;
import com.rahul.ailoganalyzer.extractor.RelevantLogExtractor;
import com.rahul.ailoganalyzer.parser.LogFileParser;
import com.rahul.ailoganalyzer.prompt.LogAnalysisPromptBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LogAnalysisService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final RelevantLogExtractor relevantLogExtractor;
    private final LogFileParser logFileParser;
    private final LogAnalysisPromptBuilder logAnalysisPromptBuilder;
    private final LogAnalysisProperties  logAnalysisProperties;

    public LogAnalysisService(ChatClient.Builder chatClientBuilder,
                              ObjectMapper objectMapper,
                              RelevantLogExtractor relevantLogExtractor,
                              LogFileParser logFileParser,
                              LogAnalysisPromptBuilder logAnalysisPromptBuilder,
                              LogAnalysisProperties  logAnalysisProperties) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
        this.relevantLogExtractor = relevantLogExtractor;
        this.logFileParser = logFileParser;
        this.logAnalysisPromptBuilder = logAnalysisPromptBuilder;
        this.logAnalysisProperties = logAnalysisProperties;
    }

    public LogAnalysisResponse analyzeLogs(LogAnalysisRequest request) {
        log.info("Starting AI log analysis for raw log input");
        String prompt = logAnalysisPromptBuilder.buildPrompt(request.getLogs());
        return executeAiAnalysis(prompt);
    }

    public LogFileAnalysisResponse analyzeLogFile(MultipartFile file) {
        String fileContent = logFileParser.parse(file);

        List<String> extractedBlocks = relevantLogExtractor.extractRelevantBlocks(fileContent);
        log.info("Processing {} extracted incident block(s) from uploaded file", extractedBlocks.size());

        List<LogAnalysisResponse> incidents = new ArrayList<>();

        for (int i = 0; i < extractedBlocks.size(); i++) {
            String block = extractedBlocks.get(i);

            if (block.length() > logAnalysisProperties.getMaxPromptLogLength()) {
                log.warn("Incident block {} exceeded max prompt length. Truncating from {} to {} characters",
                        i + 1, block.length(), logAnalysisProperties.getMaxPromptLogLength());
                block = block.substring(0, logAnalysisProperties.getMaxPromptLogLength());
            }

            log.info("Analyzing incident block {}", i + 1);

            String prompt = logAnalysisPromptBuilder.buildPrompt(block);
            LogAnalysisResponse response = executeAiAnalysis(prompt);
            incidents.add(response);
        }

        LogFileAnalysisResponse result = new LogFileAnalysisResponse();
        result.setIncidentCount(incidents.size());
        result.setIncidents(incidents);

        return result;
    }

    private LogAnalysisResponse executeAiAnalysis(String prompt) {
        String aiResponse;
        try {
            log.info("Calling OpenAI for log analysis");
            aiResponse = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            log.info("Received response from OpenAI");
        } catch (Exception e) {
            log.error("OpenAI call failed during log analysis", e);
            throw new AiAnalysisException("Failed to analyze logs");
        }

        if (aiResponse == null || aiResponse.isBlank()) {
            log.warn("AI returned empty or blank response");
            throw new AiAnalysisException("Failed to analyze logs");
        }

        aiResponse = aiResponse.replace("```json", "")
                .replace("```", "")
                .trim();

        try {
            LogAnalysisResponse response = objectMapper.readValue(aiResponse, LogAnalysisResponse.class);
            log.info("Successfully parsed AI response");
            return response;
        } catch (Exception e) {
            log.error("Failed to parse AI response", e);
            log.debug("Sanitized AI response: {}", aiResponse);
            throw new AiAnalysisException("Failed to analyze logs");
        }
    }
}