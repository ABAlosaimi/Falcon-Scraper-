package com.proxy.falcon.Proxy.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ScrapingResults {

    private String[] results;


    public ScrapingResults(String[] results) {
        this.results = results;
    }   

    public ScrapingResults() {
    }   

    public String[] getResults() {
        return results;
    }

    public void setResults(String[] results) {
        this.results = results;
    }
    
}