package com.skb.onm.bluemix.app.consumer.config;

import javax.annotation.PostConstruct;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(InfluxDBProperties.class)
public class InfluxDBConfig {

	/**
	 * retentionPolicy
	 */
	@Value("${bluemix.influxdb.retention-policy}")
	private String retentionPolicy;

	/**
	 * database
	 */
	@Value("${bluemix.influxdb.database}")
	private String database;
	/**
	 * init influxdbconfig.
	 * @method name: init
	 */
	@PostConstruct
	public void init() {
		if (retentionPolicy.isEmpty() || retentionPolicy == null) {
			retentionPolicy = "autogen";
		}
	}
	
	/**
	 * set influxdb properties.
	 * 
	 * @method name: influxdbFactory
	 * @param properties influxdb properties
	 * @return InfluxDB
	 */
	@Bean
	public InfluxDB influxdbFactory(InfluxDBProperties properties) {
		InfluxDB influxDB = InfluxDBFactory.connect(properties.getUrl(), properties.getUser(),
				properties.getPassword());
		/*
		 * if (retentionPolicy.isEmpty() { retentionPolicy = "autogen"; }
		 */
		influxDB.setRetentionPolicy(retentionPolicy);
		influxDB.setDatabase(database);
		return influxDB;
	}
	
}
