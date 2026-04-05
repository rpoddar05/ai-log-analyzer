package com.rahul.ailoganalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogFileAnalysisResponse {

    private int incidentCount;
    private List<LogAnalysisResponse> incidents;

}
