package com.campus.campus_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = "uploads";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Resolve absolute path to uploads folder
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
        String uploadsAbsolute = uploadPath.toString();

        // ⭐ Serve uploaded profile images & other uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadsAbsolute + "/")
                .setCachePeriod(0);

        // Serve frontend assets (static files)
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/public/"
                );
    }
}
