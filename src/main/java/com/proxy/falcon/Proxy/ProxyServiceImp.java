package com.proxy.falcon.Proxy;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.web.client.RestTemplate;
import com.proxy.falcon.Parser.ParserService;
import com.proxy.falcon.Proxy.Dto.ScrapingResults;

@Service
public class ProxyServiceImp implements ProxyService {

    private final List<String> userAgents;
    private final SecureRandom random;
    private final RestTemplate restTemplate;
    @Qualifier("urlsExecutor")
    private final Executor urlsExecutor;
    private final ParserService parserService;

    public ProxyServiceImp(Executor urlsExecutor, ParserService parserService) {
        this.parserService = parserService;
        this.urlsExecutor = urlsExecutor;

        this.userAgents = Arrays.asList(
            // Chrome User Agents
       "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36",
            
            // Firefox User Agents
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
            
            // Safari User Agents
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15",
            
            // Edge User Agents
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59"
        );

        this.random = new SecureRandom();
        this.restTemplate = new RestTemplate();
    }


    @Override
    public ScrapingResults scrapAndParse(String[] parsParams,String[] urls, Map<String, String> userHeaders) 
    throws Exception {

        CompletableFuture<String[]> future = parallelScraping(urls, userHeaders);
       
        if (parsParams != null) {
            String[] results = parserService.parse(future.get(), parsParams).get();
            return new ScrapingResults(results);
        }

       return new ScrapingResults(future.get());
       
    }

   
    private CompletableFuture<String[]> parallelScraping(String[] urls, Map<String, String> userHeaders) {
        HttpHeaders requestHeaders = new HttpHeaders();
        String userAgent = getRandomUserAgent();

        requestHeaders.add("User-Agent", userAgent);
        requestHeaders.add("Accept-Language", "en-US,en;q=0.9");
        requestHeaders.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        requestHeaders.add("Connection", "keep-alive");
        requestHeaders.add("Upgrade-Insecure-Requests", "1");

        if (userHeaders != null) {
            userHeaders.forEach(requestHeaders::add);
        }

        
        @SuppressWarnings("unchecked")
        CompletableFuture<String>[] futures = Arrays.stream(urls)
            .map(url -> CompletableFuture.supplyAsync(() -> {
                try {
                    ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(requestHeaders),
                        String.class
                    );

                    return response.getBody();
                } catch (Exception e) {
                    throw new RuntimeException("Error fetching URL: " + url, e);
                }
            }, urlsExecutor))
            .toArray(size -> new CompletableFuture[size]);
            

               return CompletableFuture.allOf(futures)
                                        .thenApply(v -> Arrays.stream(futures)
                                         .map(CompletableFuture::join) 
                                          .toArray(String[]::new)
                                       );
    }


    private String getRandomUserAgent() {
        return userAgents.get(random.nextInt(userAgents.size()));
    }

    
}
