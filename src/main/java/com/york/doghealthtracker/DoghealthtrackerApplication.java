package com.york.doghealthtracker;

import com.york.doghealthtracker.config.HighlightConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class DoghealthtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoghealthtrackerApplication.class, args);
	}

}
