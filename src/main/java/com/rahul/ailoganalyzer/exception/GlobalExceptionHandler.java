package com.rahul.ailoganalyzer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AiAnalysisException.class)
    public ResponseEntity<ApiErrorResponse> handleAiAnalysisException(AiAnalysisException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                OffsetDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "AI_ANALYSIS_FAILED",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                OffsetDateTime.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                OffsetDateTime.now().toString(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}