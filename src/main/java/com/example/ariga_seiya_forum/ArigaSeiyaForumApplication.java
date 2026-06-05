package com.example.ariga_seiya_forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class ArigaSeiyaForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArigaSeiyaForumApplication.class, args);
	}

}
