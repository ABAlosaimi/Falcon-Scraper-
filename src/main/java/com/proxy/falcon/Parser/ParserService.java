package com.proxy.falcon.Parser;

import java.util.concurrent.CompletableFuture;

public interface ParserService {
    String[] parse(String[] scrapingResults, String[] parsParams) throws Exception;
}
