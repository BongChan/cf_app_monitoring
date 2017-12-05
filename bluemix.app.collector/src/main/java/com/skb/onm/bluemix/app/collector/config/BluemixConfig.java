/*
 * Copyright (c) 2017. 11. 7. SK C&C Co., Ltd. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with SK
 * C&C.
 */
package com.skb.onm.bluemix.app.collector.config;

import java.util.List;
import java.util.stream.Collectors;

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>업무 그룹명 : onm-collector-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.collector.config</li>
 * <li>설 명 : BluemixConfig</li>
 * <li>작성일 : 2017. 11. 7.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(BluemixProperties.class)
public class BluemixConfig {

    @Autowired
    private BluemixProperties bluemixProperties;

    /**
     * cloudFoundryClients
     *  
     * @return List<ReactorCloudFoundryClient>
     */
    /*@Bean
    public List<ReactorCloudFoundryClient> cloudFoundryClients() {
        List<BluemixProperty> bluemixPropertyLists = bluemixProperties.getBluemixProperties();
        List<ReactorCloudFoundryClient> reactorCloudFoundryClients = bluemixPropertyLists.stream()
            .map(bluemixProperty -> {
                ReactorCloudFoundryClient reactorCloudFoundryClient = ReactorCloudFoundryClient.builder()
                    .connectionContext(
                        this.connectionContext(bluemixProperty.getIp(), bluemixProperty.getSkipSslValidation()))
                    .tokenProvider(this.tokenProvider(bluemixProperty.getPassword(), bluemixProperty.getUser()))
                    .build();
                return reactorCloudFoundryClient;
            })
            .collect(Collectors.toList());

        return reactorCloudFoundryClients;
    }*/
    
    @Bean
    public List<BluemixProperty> createBluemixProperties()	{
    	return bluemixProperties.getBluemixProperties().stream()
				.map(bluemixProperty -> {
					ReactorCloudFoundryClient reactorCloudFoundryClient = ReactorCloudFoundryClient.builder()
							.connectionContext(this.connectionContext(bluemixProperty.getIp(),
									bluemixProperty.getSkipSslValidation()))
							.tokenProvider(this.tokenProvider(bluemixProperty.getPassword(), bluemixProperty.getUser()))
							.build();
					bluemixProperty.setReactorCloudFoundryClient(reactorCloudFoundryClient);
					return bluemixProperty;
				}).collect(Collectors.toList());
    	
/*    	//return createdBluemixProperties;
    	
x   	List<BluemixProperty> bluemixPropertyLists = bluemixProperties.getBluemixProperties();
    	List<BluemixProperty> createdBluemixProperties = bluemixPropertyLists.stream()
        .map(bluemixProperty -> {
            ReactorCloudFoundryClient reactorCloudFoundryClient = ReactorCloudFoundryClient.builder()
                .connectionContext(
                    this.connectionContext(bluemixProperty.getIp(), bluemixProperty.getSkipSslValidation()))
                .tokenProvider(this.tokenProvider(bluemixProperty.getPassword(), bluemixProperty.getUser()))
                .build();
            bluemixProperty.setReactorCloudFoundryClient(reactorCloudFoundryClient);
            return bluemixProperty;
        }).
        collect(Collectors.toList());
    	
    	return createdBluemixProperties;*/
    }

    private DefaultConnectionContext connectionContext(String apiHost, Boolean skipSslValidation) {
        DefaultConnectionContext defaultConnectionContext = DefaultConnectionContext.builder()
            .apiHost(apiHost)
            .skipSslValidation(skipSslValidation)
            .build();
        //log.info("################ connectionTimeout config ############# - {}", defaultConnectionContext.getConnectTimeout().get().toString());
        return defaultConnectionContext;
    }

    private TokenProvider tokenProvider(String password, String username) {
        TokenProvider tokenProvider = PasswordGrantTokenProvider.builder()
            .password(password)
            .username(username)
            .build();
        return tokenProvider;
    }
    
    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }
    
}
