package com.aha.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.aha.mcp", "com.aha.common"})
public class AhaMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(AhaMcpApplication.class, args);
    }
}
