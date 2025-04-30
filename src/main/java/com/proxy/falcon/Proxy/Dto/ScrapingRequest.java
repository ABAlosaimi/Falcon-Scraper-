package com.proxy.falcon.Proxy.Dto;

import lombok.Data;

@Data
public class ScrapingRequest {
    
    String[] urls;
    String[] parsParams; 

}
