package com.proxy.falcon.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.proxy.falcon.Exception.ParsePageException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ParserServiceImp implements ParserService {

    public ParserServiceImp() {
    }


    public String[] parse(String[] scrapingResults, Map<String, String> parsParams) throws Exception {
       
        if (scrapingResults == null || scrapingResults.length == 0) {
            throw new IllegalArgumentException("No HTML pages provided for parsing.");
        }
        if (parsParams == null || parsParams.size() == 0) {
            throw new IllegalArgumentException("No parsing parameters provided.");
        }

       
      CompletableFuture<String[]>[] futures = Arrays.stream(scrapingResults)
                .map(result -> CompletableFuture.supplyAsync(() -> {
                    try {
                        List<String> combinedResults = new ArrayList<>(); // for each page 

                        parsParams.forEach((key, value) -> {
                            // Parse the HTML string into a Jsoup Document
                            Document document = Jsoup.parse(result);

                            // Example: Extract all links (anchor tags)
                            Elements extractParseParam = document.select(key);

                            extractParseParam.stream()
                                    .map(element -> element.select(value).text())
                                    .forEach(combinedResults::add);
                        });

                        return combinedResults.toArray(new String[0]); 
                    } catch (RuntimeException e) {
                         throw new ParsePageException("Error parsing the page: " + result + "check the parsing parameters");
                    }
                }))
                .toArray(CompletableFuture[]::new);

        String[] parsedData = Arrays.stream(futures)
                .map(future -> ((CompletableFuture<String[]>) future).join())
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        return parsedData;

    }
}
