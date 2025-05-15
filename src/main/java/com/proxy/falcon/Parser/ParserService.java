package com.proxy.falcon.Parser;

public interface ParserService {
    String[] parse(String[] scrapingResults, String[] parsParams) throws Exception;
}
