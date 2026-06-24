package com.kkobau.earthquakemonitor;

import org.springframework.boot.SpringApplication;

public class TestEarthquakemonitorApplication {

	public static void main(String[] args) {
		SpringApplication.from(EarthquakemonitorApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
