package com.kkobau.earthquakemonitor;

import com.kkobau.earthquakemonitor.config.ClientProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ClientProperties.class)
public class EarthquakemonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EarthquakemonitorApplication.class, args);
	}

}
