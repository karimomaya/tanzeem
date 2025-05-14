package com.tanzeem.iam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.tanzeem.iam", "com.tanzeem.security.common"})
public class IamApplication {
    public static void main(String[] args) {
        SpringApplication.run(IamApplication.class, args);
    }
}
