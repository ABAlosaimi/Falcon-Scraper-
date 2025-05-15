package com.proxy.falcon.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.proxy.falcon.Exception.ParsePageException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ParserServiceImp implements ParserService {

    public ParserServiceImp() {
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
                    } catch (RuntimeException e) {
                         throw new ParsePageException("Error parsing the page: " + result + "check the parsing parameters");
                    }
                }))
                .toArray(CompletableFuture[]::new);

        
        return Arrays.stream(futures)
                .map(future -> ((CompletableFuture<String[]>) future).join())
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

    }
}
