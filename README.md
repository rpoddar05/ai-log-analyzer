# AI Log Analyzer

AI-powered log analysis service built with Java, Spring Boot, Spring AI, and OpenAI to process raw application logs or uploaded log files and return structured root-cause analysis.

## Overview

Production log files are noisy, large, and expensive to send directly to an LLM. This project improves that workflow by adding deterministic preprocessing before the AI call.

The system can:
- accept raw log text
- accept uploaded `.log` or `.txt` files
- extract relevant error windows and stack trace context
- send only reduced, high-signal content to the LLM
- return structured analysis as JSON

## Features

- Spring Boot REST API
- Raw log text analysis endpoint
- Multipart log file upload endpoint
- Deterministic log extraction before LLM invocation
- Structured JSON output:
    - summary
    - rootCause
    - severity
    - suggestedFix
- Centralized exception handling
- Request validation
- Configurable file size, prompt length, and extraction window

## Architecture

```text
Client
 -> Controller
 -> LogFileParser
 -> RelevantLogExtractor
 -> LogAnalysisPromptBuilder
 -> OpenAI (via Spring AI)
 -> JSON parsing into DTO
 -> Structured API response