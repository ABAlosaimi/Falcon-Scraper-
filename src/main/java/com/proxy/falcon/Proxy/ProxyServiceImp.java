package com.proxy.falcon.Proxy;

import org.springframework.cache.annotation.Cacheable;
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
import com.proxy.falcon.Exception.UnvalidURLException;
import com.proxy.falcon.Parser.ParserService;
import com.proxy.falcon.Proxy.Dto.ScrapingResults;
import com.proxy.falcon.cleaner.CleaningService;

@Service
public class ProxyServiceImp implements ProxyService {

    private final List<String> userAgents;
    private final SecureRandom random;
    private final RestTemplate restTemplate;
    private final Executor urlsExecutor;
    private final ParserService parserService;
    private final CleaningService cleaningService;

    public ProxyServiceImp(Executor urlsExecutor, ParserService parserService, CleaningService cleaningService) {
        this.cleaningService = cleaningService;
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
    @Cacheable(value = "scrapingCache", key = "#urls")
    public ScrapingResults scrapAndParse(String[] urls,String[] parsParams,String[] cleanParams, Map<String, String> userHeaders) 
    throws Exception {

        CompletableFuture<String[]> scrapedData = parallelScraping(urls, userHeaders);
       
        if (parsParams != null && cleanParams != null) {
            String[] parsedStrings = parserService.parse(scrapedData.get(), parsParams);
            String[] results = cleaningService.clean(parsedStrings, cleanParams);

            return new ScrapingResults(results);

        }else if (parsParams != null) {
            String[] parsedStrings = parserService.parse(scrapedData.get(), parsParams);
            return new ScrapingResults(parsedStrings);

        } else if (cleanParams != null) {
            String[] cleanedStrings = cleaningService.clean(scrapedData.get(), cleanParams);
            return new ScrapingResults(cleanedStrings);
        }

       return new ScrapingResults(scrapedData.get());
       
    }

   
    private CompletableFuture<String[]> parallelScraping(String[] urls, Map<String, String> userHeaders) throws Exception {
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
                    throw new UnvalidURLException("Error fetching URL:" + url);
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
