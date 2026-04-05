package com.rahul.ailoganalyzer.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogAnalysisResponse {

    private String summary;
    private String rootCause;
    private String severity;
    private String suggestedFix;


}
