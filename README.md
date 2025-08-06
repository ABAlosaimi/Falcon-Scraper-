# Falcon Web Scraping 

Falcon is a Spring Boot-based web scraping service that allows users to fetch, parse, and clean HTML content from multiple URLs in parallel. It provides a robust, asynchronous handling of requests, API for extracting structured data from web pages (HTML), with built-in validation and cleaning capabilities.

## Features
- **Parallel Web Scraping:** Fetch multiple web pages concurrently using a configurable thread pool. that enables effictive without being blocked by slow responses or timeouts. 
- **HTML Parsing:** Extract specific elements from the HTML based on the provided JSON that specifying the needed elements from the page using CSS selectors (e.g., links, images, headings).
- **Data Cleaning:** Clean parsed text with options like lowercasing, removing numbers, special characters, and trimming whitespace.
- **Validation:** Ensures only valid HTTP/HTTPS URLs and safe parsing parameters are processed.
- **Extensible:** Easily add new parsing or cleaning strategies.

## Technologies Used
- Java 21
- Spring Boot
- Spring Web
- Spring Async
- Jsoup (HTML parsing)
- Lombok

## Project Structure
```
src/
  main/
    java/
      com/proxy/falcon/
        Proxy/           # Proxy service, controller, configs, DTOs
        Parser/          # HTML parsing service
        cleaner/         # Data cleaning service
        Exception/       # Custom exceptions and global exception handler 
```

## API Endpoints

### 1. Scraping Endpoint
**POST** `/proxy/api/scrap`

#### Request Body
Send a JSON object matching the `ScrapingRequest` DTO:
```json
{
  "urls": ["https://example.com", "https://another.com"],
  "parsParams": ["a[href]", "img[src]"],
  "cleanParams": ["lowercase", "remove_numbers", "strip_whitespace"]
}
```
- `urls`: Array of HTTP/HTTPS URLs to scrape.
- `parsParams`: Array of CSS selectors for parsing HTML (e.g., `a[href]`, `img[src]`, `h1`).
- `cleanParams`: Array of cleaning operations (`lowercase`, `remove_numbers`, `remove_special_chars`, `strip_whitespace`).

#### Optional Headers
You can send custom headers as a second JSON object in the request body (if supported by your client), or as HTTP headers.

#### Response
Returns a JSON object matching the `ScrapingResults` DTO:
```json
{
  "results": [
    "example link text",
    "another link text",
    "image alt text"
  ]
}
```
- `results`: Array of cleaned, parsed strings extracted from the provided URLs.

#### Error Handling
- Returns `400 Bad Request` for invalid URLs, parsing parameters, or cleaning parameters.


## How It Works
1. **Validation:** Incoming URLs and parsing parameters are validated for safety and correctness.
2. **Parallel Scraping:** URLs are fetched in parallel using a thread pool.
3. **Parsing:** HTML content is parsed using Jsoup and the provided CSS selectors.
4. **Cleaning:** Parsed strings are cleaned according to the specified cleaning parameters.
5. **Response:** The cleaned, parsed results are returned as a JSON array.

## Example Usage
### cURL
```sh
curl -X POST http://localhost:8080/proxy/api/scrap \
  -H "Content-Type: application/json" \
  -d '{
    "urls": ["https://example.com"],
    "parsParams": ["a[href]"],
    "cleanParams": ["lowercase", "strip_whitespace"]
  }'
```

## Configuration
Thread pool settings and other configurations can be adjusted in `ProxyConfigs.java` or via `application.properties` in your env.

## Contact
For questions or contributions, please open an issue or pull request on GitHub or you can contact me on X @AFA_24a. 
