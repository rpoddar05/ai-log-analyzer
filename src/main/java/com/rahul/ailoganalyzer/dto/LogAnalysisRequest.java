package com.rahul.ailoganalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogAnalysisRequest {
    @NotBlank(message = "logs is required")
    private String logs;

}
