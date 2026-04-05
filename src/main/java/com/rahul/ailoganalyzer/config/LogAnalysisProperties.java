package com.rahul.ailoganalyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app.log-analysis")
public class LogAnalysisProperties {

    private long maxFileSizeBytes;
    private int maxPromptLogLength;
    private int maxFallbackLogLength;
    private int contextBefore;
    private int contextAfter;

}
