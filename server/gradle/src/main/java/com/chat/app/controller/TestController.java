package com.chat.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/link-preview")
    public ResponseEntity<?> getLinkPreview(@RequestParam String url) {
        try {
            return ResponseEntity.ok(Map.of("link", url));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch link preview");
        }
    }
}
