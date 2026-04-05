package com.rahul.ailoganalyzer.prompt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogAnalysisPromptBuilder {

    public String buildPrompt(String reducedLogContent) {

        log.debug("Building AI prompt. Reduced log content length: {}",
                reducedLogContent != null ? reducedLogContent.length() : 0);


        return """
                You are an expert production support engineer.

                Analyze the following extracted application log incidents.

                STRICT RULES:
                - Return ONLY valid JSON
                - Do NOT include markdown
                - Do NOT include explanations
                - Do NOT include extra text

                Required JSON format:
                {
                  "summary": "...",
                  "rootCause": "...",
                  "severity": "...",
                  "suggestedFix": "..."
                }

                Focus on:
                - most probable root cause
                - severity of the most important issue
                - best suggested fix

                Extracted log content:
                %s
                """.formatted(reducedLogContent);
    }
}
