
package com.employee.tracking.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class SecurityTestController {

    @GetMapping("/me")
    public Map<String, String> currentUser(
            Authentication authentication) {

        return Map.of(
            "message", "Authentication successful",
            "email", authentication.getName(),
            "authorities", authentication.getAuthorities().toString()
        );
    }
}
