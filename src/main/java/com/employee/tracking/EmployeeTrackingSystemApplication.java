package com.employee.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class EmployeeTrackingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeTrackingSystemApplication.class, args);
	}

}
