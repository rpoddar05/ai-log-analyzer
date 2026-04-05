package com.rahul.ailoganalyzer.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private String timestamp;
    private int status;
    private String error;
    private String message;

}
