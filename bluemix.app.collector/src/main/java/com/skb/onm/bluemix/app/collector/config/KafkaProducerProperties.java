package com.skb.onm.bluemix.app.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluemix.kafka.producer")
public class KafkaProducerProperties {

	private String bootstrapServersConfigs;
	private String acks;
	private int retriesConfigs;
	private int batchSizeConfigs;
	private long lingerMsConfig;
	private long bufferMemoryConfig;
	private String keySerializerClassConfig;
	private String valueSerializerClassConfig;

}
