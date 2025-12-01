package com.proxy.falcon.Proxy;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proxy.falcon.Exception.RequestBodyException;
import com.proxy.falcon.Proxy.Dto.ScrapingRequest;
import com.proxy.falcon.Proxy.Dto.ScrapingResults;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService){
        this.proxyService = proxyService;
    }


    @PostMapping("/v1/api/scrap/") 
    public ResponseEntity<ScrapingResults> scrap(@RequestBody @Valid ScrapingRequest scrapingRequest) 
    throws Exception{

     ScrapingResults results = proxyService.scrapAndParse(scrapingRequest.getUrls(),
                                                          scrapingRequest.getParsParams(),
                                                          scrapingRequest.getCleanParams(),
                                                          scrapingRequest.getUserHeaders());

     return ResponseEntity.ok().body(results);

    }

}