package com.proxy.falcon.Parser;

import java.util.concurrent.CompletableFuture;

public interface ParserService {
    CompletableFuture<String[]> parse(String[] scrapingResults, String[] parsParams) throws Exception;
}
