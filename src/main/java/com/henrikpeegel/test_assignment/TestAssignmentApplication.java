package com.henrikpeegel.test_assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TestAssignmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestAssignmentApplication.class, args);
	}
}