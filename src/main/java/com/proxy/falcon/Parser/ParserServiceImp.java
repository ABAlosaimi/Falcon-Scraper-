package com.proxy.falcon.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ParserServiceImp implements ParserService {

    /**
     * Parses an array of HTML strings and extracts specific elements.
     *
     * @param htmlPages Array of HTML strings to parse.
     * @return List of extracted data (e.g., links or other elements).
     */
    public CompletableFuture<String[]> parse(String[] scrapingResults, String[] parsParams) throws Exception {
       
        if (scrapingResults == null || scrapingResults.length == 0) {
            throw new IllegalArgumentException("No HTML pages provided for parsing.");
        }
        if (parsParams == null || parsParams.length == 0) {
            throw new IllegalArgumentException("No parsing parameters provided.");
        }

       
      CompletableFuture<String[]>[] futures = Arrays.stream(scrapingResults)
                .map(result -> CompletableFuture.supplyAsync(() -> {
                    try {
                        List<String> combinedResults = new ArrayList<>(); // for the each page 

                        Arrays.stream(parsParams).forEach(param -> {
                            // Parse the HTML string into a Jsoup Document
                            Document document = Jsoup.parse(result);

                            // Example: Extract all links (anchor tags)
                            Elements extractParseParam = document.select(param);

                            extractParseParam.stream()
                                    .map(element -> element.attributes().toString())
                                    .forEach(combinedResults::add);
                        });

                        return combinedResults.toArray(); 
                    } catch (Exception e) {
                        // Handle parsing errors for individual pages
                        return new String[]{"Error parsing page: " + e.getMessage()};
                    }
                }))
                .toArray(CompletableFuture[]::new);

        
        return CompletableFuture.allOf(futures)
                                .thenApply(v -> Arrays.stream(futures)
                                .map(CompletableFuture::join) 
                                .toArray(String[]::new)
       );

    }
}
