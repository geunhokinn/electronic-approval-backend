package com.comin.eapproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class ElectronicApprovalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicApprovalApplication.class, args);
	}

}
