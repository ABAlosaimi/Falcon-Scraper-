package com.proxy.falcon.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class ParserServiceImp implements ParserService {

    private final Executor urlsExecutor;

    public ParserServiceImp(Executor urlsExecutor) {
        this.urlsExecutor = urlsExecutor;
    }


    public String[] parse(String[] scrapingResults, String[] parsParams) throws Exception {
       
        if (scrapingResults == null || scrapingResults.length == 0) {
            throw new IllegalArgumentException("No HTML pages provided for parsing.");
        }
        if (parsParams == null || parsParams.length == 0) {
            throw new IllegalArgumentException("No parsing parameters provided.");
        }

       
      CompletableFuture<String[]>[] futures = Arrays.stream(scrapingResults)
                .map(result -> CompletableFuture.supplyAsync(() -> {
                    try {
                        List<String> combinedResults = new ArrayList<>(); // for each page 

                        Arrays.stream(parsParams).forEach(param -> {
                            // Parse the HTML string into a Jsoup Document
                            Document document = Jsoup.parse(result);

                            // Example: Extract all links (anchor tags)
                            Elements extractParseParam = document.select(param);

                            extractParseParam.stream()
                                    .map(element -> element.attributes().toString())
                                    .forEach(combinedResults::add);
                        });

                        return combinedResults.toArray(new String[0]); 
                    } catch (Exception e) {
                        // Handle parsing errors for individual pages
                        return new String[]{"Error parsing page: " + e.getMessage()};
                    }
                }))
                .toArray(CompletableFuture[]::new);

        
        return Arrays.stream(futures)
                .map(future -> ((CompletableFuture<String[]>) future).join())
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

    }
}
