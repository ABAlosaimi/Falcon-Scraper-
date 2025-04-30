package com.proxy.falcon.Proxy;

import java.util.Map;

import com.proxy.falcon.Proxy.Dto.ScrapingResults;

public interface ProxyService {
       ScrapingResults scrapAndParse(String[] parsParams,String[] url,Map<String, String> userHeaders) throws Exception;  // async
      
 }
