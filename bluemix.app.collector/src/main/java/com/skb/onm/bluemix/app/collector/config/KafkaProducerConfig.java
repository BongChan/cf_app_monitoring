package com.skb.onm.bluemix.app.collector.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

	@Autowired
	private KafkaProducerProperties properties;

	/**
	 * set kafka producer configs from application.yml.
	 * 
	 * @method name: producerConfigs.
	 * @return kafka producer configs
	 */

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServersConfigs());
		props.put(ProducerConfig.ACKS_CONFIG, properties.getAcks());
		props.put(ProducerConfig.RETRIES_CONFIG, properties.getRetriesConfigs());
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getBatchSizeConfigs());
		props.put(ProducerConfig.LINGER_MS_CONFIG, properties.getLingerMsConfig());
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getBufferMemoryConfig());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				properties.getKeySerializerClassConfig());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				properties.getValueSerializerClassConfig());
		return props;
	}

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate(
			ProducerFactory<String, String> producerFactory) {
		return new KafkaTemplate<String, String>(producerFactory);
	}

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}

}
