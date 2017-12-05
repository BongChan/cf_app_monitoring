/*******************************************************************************
 *
 * Copyright (c) 2017 SK C&C Co., Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with SK
 * C&C.
 *******************************************************************************/
package com.skb.onm.bluemix.app.consumer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <ul>
 * <li>업무 그룹명 : onm-consumer-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.bluemix.consumer.config</li>
 * <li>설  명 : KafkaConsumerConfig</li>
 * <li>작성일 : 2017. 10. 26.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */
@Configuration
@EnableKafka
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

    /**
     * pollTimeout
     */
    @Value("${bluemix.kafka.pollTimeout}")
    private int pollTimeout;

    /**
     * concurrency
     */
    @Value("${bluemix.kafka.concurrency}")
    private int concurrency;
    
    /**
     * properties
     */
    @Autowired
    private KafkaConsumerProperties properties;

    // @Autowired
    // private MessageListener<String, String> messageListener;

    
    /**
     * consumerProps
     *  
     * @return Map<String,Object>
     */
    @Bean
    public Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootStrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.isEnableAutoCommit());
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.getAutoCommitIntervalMs());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeoutMs());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getKeyDeserializerClassConfig());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.getValueDeserializerClassConifg());

        return props;
    }

    /**
     * consumerFactory
     *  
     * @return ConsumerFactory<String,String>
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        DefaultKafkaConsumerFactory<String, String> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
            consumerProps());
        return defaultKafkaConsumerFactory;
    }

    /**
     * kafkaListenerContainerFactory
     *  
     * @return KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>>
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(1000);
        
        return factory;
    }

    /**
     * objectMapper
     *  
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }

}
