package com.tanzeem.purchase;

import com.tanzeem.security.config.FeignClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication(scanBasePackages = {"com.tanzeem.purchase", "com.tanzeem.security.common", "com.tanzeem.common"})
@EnableJpaAuditing
@EnableFeignClients(basePackages = {
        "com.tanzeem.common.client",
        "com.tanzeem.purchase.client" // if any
}, defaultConfiguration = FeignClientConfig.class)
public class PurchaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(PurchaseApplication.class, args);
    }
}
