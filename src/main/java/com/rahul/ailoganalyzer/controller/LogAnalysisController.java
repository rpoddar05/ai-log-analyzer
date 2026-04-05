package com.rahul.ailoganalyzer.controller;

import com.rahul.ailoganalyzer.dto.LogAnalysisRequest;
import com.rahul.ailoganalyzer.dto.LogAnalysisResponse;
import com.rahul.ailoganalyzer.dto.LogFileAnalysisResponse;
import com.rahul.ailoganalyzer.service.LogAnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/log-analysis")
public class LogAnalysisController {

    private final LogAnalysisService logAnalysisService;

    public LogAnalysisController(LogAnalysisService logAnalysisService) {
        this.logAnalysisService = logAnalysisService;
    }

    @PostMapping("/analyze")
    public LogAnalysisResponse analyze(@Valid @RequestBody LogAnalysisRequest logAnalysisRequest) {
        return logAnalysisService.analyzeLogs(logAnalysisRequest);
    }

    @PostMapping(value ="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public LogFileAnalysisResponse analyzeLogFile(@RequestParam("file")  MultipartFile file) {
        return logAnalysisService.analyzeLogFile(file);
    }
}
