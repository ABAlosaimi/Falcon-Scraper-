package com.proxy.falcon.cleaner;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class CleaningServiceImp implements CleaningService{

    public CleaningServiceImp() {
    }

    @Override
    public String[] clean(String[] parsedStrings, String[] cleanParams) throws Exception {
        
        if (cleanParams == null || cleanParams.length == 0) {
            throw new IllegalArgumentException("No cleaning parameters provided.");
        }

    CompletableFuture<String>[] futures = Arrays.stream(parsedStrings)
        .map(ps -> CompletableFuture.supplyAsync(() -> {
            String result = ps;
            try {
                for (String cp : cleanParams) {
                    switch (cp) {
                        case "lowercase":
                            result = result.toLowerCase();
                            break;
                        case "remove_numbers":
                            result = result.replaceAll("\\d+", "");
                            break;
                        case "remove_special_chars":
                            result = result.replaceAll("[^a-zA-Z0-9\\s]", "");
                            break;
                        case "strip_whitespace":
                            result = result.trim();
                            break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return result;
        }))
        .toArray(CompletableFuture[]::new);

    String[] cleaned = Arrays.stream(futures)
        .map(future -> ((CompletableFuture<String>) future).join())
        .toArray(String[]::new);

    return cleaned;
    }
    
}
