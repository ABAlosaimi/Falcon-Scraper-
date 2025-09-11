package com.proxy.falcon.Proxy.Dto;

import java.util.Map;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ScrapingRequest {
    
    @Size(max = 10, min = 1, message = "URLs array must not contain more than 10 elements and must not be empty")
    String[] urls;
    
    @Size(max = 20, message = "Parse parameters array must not contain more than 20 elements")
    Map<String, String> parsParams; 
    
    @Size(max = 20, message = "Clean parameters array must not contain more than 20 elements")
    String[] cleanParams;
    
    @Size(max = 15, message = "User headers map must not contain more than 15 entries")
    Map<String, String> userHeaders;

}
