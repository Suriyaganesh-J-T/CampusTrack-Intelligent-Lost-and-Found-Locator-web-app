package com.campus.campus_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Only needed if you want to compute uploads absolute path dynamically.
    private final Path uploadsLocation = Paths.get("uploads"); // relative to app working dir

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /uploads/** to filesystem directory ./uploads/
        // "file:" prefix is required to serve files from filesystem.
        String uploadsAbsolute = uploadsLocation.toAbsolutePath().toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsAbsolute)
                .setCachePeriod(0); // disable caching in dev, remove or increase in production
    }
}
