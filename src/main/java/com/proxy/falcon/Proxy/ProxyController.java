package com.proxy.falcon.Proxy;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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


    @PostMapping("/api/scrap")
    public ResponseEntity<Object> scrap(@RequestBody ScrapingRequest scrapingRequest, @RequestBody(required = false) Map<String, String> userHeaders) 
    throws Exception{
    
     ScrapingResults results = proxyService.scrapAndParse(scrapingRequest.getUrls(),
                                                          scrapingRequest.getParsParams(),
                                                          scrapingRequest.getCleanParams(),
                                                          userHeaders);

     return ResponseEntity.ok().body(results);

    }


    @GetMapping("/api/hi")
    public ResponseEntity<Object> hi() {
        return ResponseEntity.ok().body("hi");
    }
     
}