/*******************************************************************************
 *
 * Copyright (c) 2017 SK C&C Co., Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 *******************************************************************************/
package com.skb.onm.bluemix.app.consumer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <ul>
 * <li>업무 그룹명 : onm-consumer-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.bluemix.consumer.config</li>
 * <li>설  명 : KafkaConsumerProperties</li>
 * <li>작성일 : 2017. 10. 26.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */
@Data
@ConfigurationProperties(prefix = "bluemix.kafka.consumer")
public class KafkaConsumerProperties {

	/**
	 * bootStrapServers
	 */
	private String bootStrapServers;
	/**
	 * groupId
	 */
	private String groupId;
	/**
	 * autoOffsetReset
	 */
	private String autoOffsetReset;
	/**
	 * enableAutoCommit
	 */
	private boolean enableAutoCommit;
	/**
	 * autoCommitIntervalMs
	 */
	private int autoCommitIntervalMs;
	/**
	 * sessionTimeoutMs
	 */
	private int sessionTimeoutMs;
	/**
	 * keyDeserializerClassConfig
	 */
	private String keyDeserializerClassConfig;
	/**
	 * valueDeserializerClassConifg
	 */
	private String valueDeserializerClassConifg;

}
