package com.adminapplicationmaster.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {
    
    // Forward all non-API routes to React app
    // All API routes should be under /api/* to avoid conflicts
    @GetMapping(value = {
        "/",
        "/login",
        "/register",
        "/dashboard",
        "/loan-applications",
        "/loan-applications/**",
        "/users",
        "/users/**"
    })
    public String forwardToReact() {
        return "forward:/index.html";
    }
}