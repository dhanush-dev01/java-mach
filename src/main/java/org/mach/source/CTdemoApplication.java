package org.mach.source;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CTdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CTdemoApplication.class, args);
	}

}
