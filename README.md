# AI Log Analyzer

AI-powered log analysis service built with Java, Spring Boot, and OpenAI that transforms raw application logs or uploaded log files into structured root-cause insights.

---

## Why this matters

Production logs are noisy, large, and expensive to process with LLMs. Sending raw logs directly to AI leads to:

* High token cost
* Low signal-to-noise ratio
* Inaccurate analysis

This project solves that by introducing a **deterministic preprocessing layer** that extracts only high-signal error sections before invoking the LLM.

---

## Features

* Spring Boot REST API
* Analyze raw log text
* Upload `.log` / `.txt` files
* Deterministic log extraction (errors + stack traces + context)
* Reduced token usage before AI call
* Structured JSON output:

  * `summary`
  * `rootCause`
  * `severity`
  * `suggestedFix`
* Centralized exception handling
* Request validation
* Configurable limits (file size, context window, prompt size)

---

## Architecture

Client
→ Controller
→ LogFileParser
→ RelevantLogExtractor
→ LogAnalysisPromptBuilder
→ OpenAI (via Spring AI)
→ JSON parsing into DTO
→ Structured API response

---

## API Endpoints

### 1. Analyze Raw Logs

**POST** `/api/v1/log-analysis/analyze`

#### Request

```json
{
  "logs": "java.lang.NullPointerException at UserService.java:45"
}
```

#### Response

```json
{
  "summary": "NullPointerException in UserService",
  "rootCause": "Accessing null object reference",
  "severity": "High",
  "suggestedFix": "Add null checks before usage"
}
```

---

### 2. Upload Log File

**POST** `/api/v1/log-analysis/upload`

* Content-Type: `multipart/form-data`
* Field name: `file`

Example:

* Upload a `.log` or `.txt` file
* System extracts relevant sections and analyzes them

---

## Configuration

Example (`application.properties`):

```properties
# Max file size (1MB)
app.log-analysis.max-file-size-bytes=1048576

# Fallback max prompt length
app.log-analysis.fallback-max-length=4000

# Context lines before/after error
app.log-analysis.context-before=2
app.log-analysis.context-after=2
```

These are **tunable limits** and can be adjusted without code changes.

---

## Key Design Decisions

* Introduced **deterministic preprocessing** before LLM invocation to reduce noise and token cost
* Extracted only:

  * error lines
  * stack traces
  * surrounding context
* Separated responsibilities into:

  * parser
  * extractor
  * prompt builder
  * service layer
* Used structured JSON output for consistent responses
* Externalized configuration for flexibility
* Added centralized exception handling for clean API responses

---

## Example Output (Multiple Incidents)

```json
{
  "incidentCount": 1,
  "incidents": [
    {
      "summary": "NullPointerException in UserService",
      "rootCause": "Null 'name' variable in UserService",
      "severity": "High",
      "suggestedFix": "Add null checks and input validation"
    }
  ]
}
```

---

## Future Improvements

* Multi-incident batching optimization
* Retry and timeout handling for AI calls
* Async processing for large log files
* Integration with ELK / Splunk / CloudWatch
* Incident deduplication and ranking

---

## Tech Stack

* Java 17
* Spring Boot
* Spring AI
* OpenAI API
* Maven

---

## Running the Project

```bash
# Clone the repo
git clone https://github.com/rpoddar05/ai-log-analyzer.git

cd ai-log-analyzer

# Run the application
./mvnw spring-boot:run
```

---

## Summary

This project demonstrates how to **effectively integrate AI into backend systems** by:

* reducing noise before LLM calls
* optimizing token usage
* generating structured, actionable insights from logs

---

## Author

Rahul Poddar
