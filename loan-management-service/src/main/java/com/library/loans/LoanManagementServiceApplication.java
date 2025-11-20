package com.library.loans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LoanManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanManagementServiceApplication.class, args);
    }
}


