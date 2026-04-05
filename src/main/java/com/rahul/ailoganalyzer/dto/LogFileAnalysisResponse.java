package com.rahul.ailoganalyzer.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogFileAnalysisResponse {

    private int incidentCount;
    private List<LogAnalysisResponse> incidents;

}
