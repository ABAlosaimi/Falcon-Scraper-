package com.proxy.falcon.Parser;

import java.util.Map;

public interface ParserService {
    String[] parse(String[] scrapingResults, Map<String, String> parsParams) throws Exception;
}
