package com.proxy.falcon.Proxy;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class ProxyConfigs implements WebMvcConfigurer{

    private UrlAndParseParamsValidationInterceptor urlValidationInterceptor;

    public ProxyConfigs(UrlAndParseParamsValidationInterceptor urlValidationInterceptor) {
        this.urlValidationInterceptor = urlValidationInterceptor;
    }
    
    @Bean
    public Executor asynExecutor(){

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("urlsAsync-");
        executor.initialize();
    
        return executor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(urlValidationInterceptor)
                .addPathPatterns("/proxy/api/scrap");
    }
    

}
