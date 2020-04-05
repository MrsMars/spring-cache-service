package com.aoher.controller;

import com.aoher.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.aoher.util.Constants.STATUS_NOT_FOUND;
import static com.aoher.util.Constants.STATUS_SUCCESS;

@RestController
@RequestMapping("/api/cache")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    private CacheService cacheService;

    @Autowired
    public TestController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PutMapping
    public ResponseEntity<?> put(@RequestParam int value) {
        try {
            cacheService.putInteger(value);
        } catch (Exception e) {
            log.error("Exception while put in cache {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(STATUS_SUCCESS);
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestParam int value) {
        try {
            return ResponseEntity.ok(cacheService.getInteger(value));
        } catch (Exception e) {
            log.error("Exception while get in cache {}", e.getMessage());
            return ResponseEntity.badRequest().body(STATUS_NOT_FOUND);
        }
    }
}
