package com.sampleapp.helloworld.e2e;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public abstract class SpringIntegration {

    protected RestTemplate restTemplate = new RestTemplate();
    @Value("${defaultURL}")
    private String defaultURL;

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getDefaultURL() {
        return defaultURL;
    }

    public void setDefaultURL(String defaultURL) {
        this.defaultURL = defaultURL;
    }
}
