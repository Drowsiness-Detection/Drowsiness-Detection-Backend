package com.khbc.drowsiness_detection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.persistence.Entity;

@Entity
@SpringBootApplication
public class DrowinessDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(DrowinessDetectionApplication.class, args);
	}

}
