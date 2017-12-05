package com.skb.onm.bluemix.app.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnmBluemixAppConsumerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(OnmBluemixAppConsumerApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
