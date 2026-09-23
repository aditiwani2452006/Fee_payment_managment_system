package com.mmcoe.feepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * MARATHWADA MITRA MANDAL'S COLLEGE OF ENGINEERING (MMCOE, PUNE)
 * Department of Information Technology - Academic Year 2026-2027
 * 
 * Project: Fee Payment Management Platform (Half-Implementation Review Build)
 * Coursework Concept Markers:
 *  - Software Engineering (SE): Modular layered architecture (Controller-Service-Repo)
 *  - Operating Systems (OS): Thread synchronization & pessimistic locking on concurrent transactions
 *  - DBMS: 14 Normalized Relational Tables (SRS Section 10) & ACID Transaction management
 *  - Computer Networks (CN): Stateless JWT authorization, REST API resources, HTTPS
 *  - Data Structures & Algorithms (DSA): In-memory payment sorting/binary search & hash indexing
 *  - Object-Oriented Programming (OOP): Encapsulation, inheritance, polymorphism
 * ============================================================================
 */
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.mmcoe.feepay")
@EnableJpaRepositories(basePackages = "com.mmcoe.feepay")
public class FeePayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeePayApplication.class, args);
        System.out.println("\n=======================================================");
        System.out.println(" MMCOE FeePay Platform (Review Build) Started Successfully!");
        System.out.println(" Swagger API Docs: http://localhost:8080/swagger-ui/index.html");
        System.out.println("=======================================================\n");
    }
}
