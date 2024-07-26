package com.nathapot.assignment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ExternalService {

    @Autowired
    private RestTemplate restTemplate;

    public String getExternalData(String url) {
        log.debug("getExternalData...");
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            log.info("Success with this url: {}", url);
            log.info("Response Status Code: {}", response.getStatusCode());
            return response.getStatusCode().toString();
        } catch (Exception e) {
            log.error("Fail with this url: {}", url, e);
            throw e;
        }
    }
}