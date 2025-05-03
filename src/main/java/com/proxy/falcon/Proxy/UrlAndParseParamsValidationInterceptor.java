package com.proxy.falcon.Proxy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Component
public class UrlAndParseParamsValidationInterceptor implements HandlerInterceptor {

    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(".*\\.{2}.*");
    private static final Pattern VALID_CSS_SELECTOR_PATTERN = Pattern.compile("^[a-zA-Z0-9.#\\[\\]=:_\\-\\s]+$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String[] urls = request.getInputStream().toString().split(","); 
        String[] parsParams = request.getInputStream().toString().split(","); 

    
        if (urls == null || urls.length == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No URLs provided.");
            return false;
        }

        for (String url : urls) {
            if (!isValidUrl(url)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL: " + url);
                return false;
            }
        }

        // Validate Parsing Parameters
        if (parsParams == null || parsParams.length == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No parsing parameters provided.");
            return false;
        }

        for (String param : parsParams) {
            if (!isValidParsingParam(param)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parsing parameter: " + param);
                return false;
            }
        }

        return true; 
    }

    private boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);

            // Check if the scheme is HTTP or HTTPS
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme())) {
                return false;
            }

            // Check for path traversal
            if (PATH_TRAVERSAL_PATTERN.matcher(uri.getPath()).matches()) {
                return false;
            }

            return true;
        } catch (URISyntaxException e) {
            return false; // Invalid URL format
        }
    }

    private boolean isValidParsingParam(String param) {
        // Check if the parameter is null or empty
        if (param == null || param.trim().isEmpty()) {
            return false;
        }

        // Check if the parameter matches the valid CSS selector pattern
        if (!VALID_CSS_SELECTOR_PATTERN.matcher(param).matches()) {
            return false;
        }

        // Check for path traversal patterns
        if (PATH_TRAVERSAL_PATTERN.matcher(param).matches()) {
            return false;
        }

        return true;
    }
}