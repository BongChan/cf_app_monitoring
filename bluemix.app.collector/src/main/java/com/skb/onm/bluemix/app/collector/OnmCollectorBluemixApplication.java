package com.skb.onm.bluemix.app.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnmCollectorBluemixApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OnmCollectorBluemixApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
