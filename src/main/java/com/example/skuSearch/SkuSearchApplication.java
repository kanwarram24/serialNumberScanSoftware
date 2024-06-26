package com.example.skuSearch;

import com.example.skuSearch.app.CustomerManagementApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SkuSearchApplication {

    public static void main(String[] args) {
        // Start the Spring Boot application (backend)
        SpringApplication.run(SkuSearchApplication.class, args);

        System.setProperty("java.awt.headless", "false");
        // Start the Java Swing frontend (call your frontend class or method here)
        startSwingFrontend();
    }

    private static void startSwingFrontend() {
        // Add code to start your Java Swing frontend here
        CustomerManagementApp swingFrontend = new CustomerManagementApp();
        swingFrontend.start();
    }
}

