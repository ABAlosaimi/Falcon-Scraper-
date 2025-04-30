package com.proxy.falcon.Proxy;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proxy.falcon.Proxy.Dto.ScrapingRequest;
import com.proxy.falcon.Proxy.Dto.ScrapingResults;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService){
        this.proxyService = proxyService;
    }


    @GetMapping("/api/scrap")
    public ResponseEntity<Object> scrap(@RequestParam(name = "urls") ScrapingRequest scrapingRequest, @RequestBody Map<String, String> userHeaders) 
    throws Exception{
    
     ScrapingResults results = proxyService.scrapAndParse(scrapingRequest.getParsParams(), scrapingRequest.getUrls(),userHeaders);

     return ResponseEntity.ok().body(results);

    }
     
}