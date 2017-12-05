/*
 * Copyright (c) 2017. 11. 7. SK C&C Co., Ltd. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C. You
 * shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with SK
 * C&C.
 */
package com.skb.onm.bluemix.app.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cloudfoundry.client.v2.applications.ApplicationStatisticsRequest;
import org.cloudfoundry.client.v2.applications.ApplicationStatisticsResponse;
import org.cloudfoundry.client.v2.applications.InstanceStatistics;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.cloudfoundry.operations.organizations.OrganizationSummary;
import org.cloudfoundry.operations.spaces.SpaceSummary;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.onm.bluemix.app.collector.config.BluemixProperty;
import com.skb.onm.bluemix.app.collector.dao.BluemixAppInfo;
import com.skb.onm.bluemix.app.collector.dao.BluemixAppInstance;
import com.skb.onm.bluemix.app.collector.dao.BluemixCollectors;

import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>업무 그룹명 : onm-collector-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.collector</li>
 * <li>설 명 : OnmBluemixCollector</li>
 * <li>작성일 : 2017. 11. 7.</li>
 * <li>작성자 : Administrator</li>
 * </ul>
 */
@Slf4j
@Component
public class OnmBluemixCollector {

	private final static String BLUEMIX_APP_INFO = "BLUEMIXAPPINFO";
	
	private final static String BLUEMIX_APP_INSTANCE_INFO = "BLUEMIXAPPINSTANCEINFO";
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate = null;

	/*@Autowired
	private List<ReactorCloudFoundryClient> clients = null;*/

	@Autowired
	private List<BluemixProperty> bluemixProperties = null;
	
	@Autowired
	private ObjectMapper objectMapper = null;
	
	@Value("${bluemix.kafka.topic}")
	private String kafkaTopic;
	
	//@Scheduled(initialDelayString = "${bluemix.collector.period}", fixedDelayString = "${bluemix.collector.period}")
	@Scheduled(fixedRateString = "${bluemix.collector.period}")
	public void collect() {
		final long currentTime = System.currentTimeMillis();
		//center 별 topic은 center 별로 클래스를 정의가 필요
		bluemixProperties.parallelStream().forEach(bluemixProperty -> {
			if(bluemixProperty != null)	{
				ReactorCloudFoundryClient client = bluemixProperty.getReactorCloudFoundryClient();
				DefaultCloudFoundryOperations operations = DefaultCloudFoundryOperations.builder()
						.cloudFoundryClient(client).build();
				
				log.info("center - {} appInfoLists - start", bluemixProperty.getCenter());
				List<BluemixAppInfo> appInfoLists = new ArrayList<>();
				List<OrganizationSummary> orgs = BluemixCollectors.getOrganizations(operations).collectList().block();
				// BluemixAppInfo appInfo = null;
				if (orgs != null) {
					for (OrganizationSummary org : orgs) {
						List<SpaceSummary> spaces = BluemixCollectors.getSpaces(operations, org.getName()).collectList()
								.block();
						for (SpaceSummary space : spaces) {
							List<ApplicationSummary> apps = BluemixCollectors
									.getApplicationSummaries(operations, org.getName(), space.getName()).collectList()
									.block();
							// apps.stream().map(application -> application).collect(Collectors.toList());
							if(apps != null)	{
								List<BluemixAppInfo> retAppInfos = apps.stream().map(app -> {
									BluemixAppInfo bluemixAppInfo = new BluemixAppInfo();
									bluemixAppInfo.setType(BLUEMIX_APP_INFO);
									bluemixAppInfo.setCenter(bluemixProperty.getCenter());
									bluemixAppInfo.setOrg(org.getName());
									bluemixAppInfo.setSpace(space.getName());
									bluemixAppInfo.setApp(app.getName());
									bluemixAppInfo.setId(app.getId());
									bluemixAppInfo.setInstances(app.getInstances());
									bluemixAppInfo.setRunningInstance(app.getRunningInstances());
									bluemixAppInfo.setDiskQuota(app.getDiskQuota());
									bluemixAppInfo.setMemoryQuota(app.getMemoryLimit());
									bluemixAppInfo.setState(app.getRequestedState());
									bluemixAppInfo.setTimeStamp(currentTime);
									return bluemixAppInfo;
								}).collect(Collectors.toList());
								appInfoLists.addAll(retAppInfos);
							}
						}
					}
				} else {
					log.info("orgs list is null");
				}
				log.info("center - {} appInfoLists - end / list size is - {}",bluemixProperty.getCenter() ,appInfoLists.size());
	
				List<BluemixAppInstance> appInstances = new ArrayList<BluemixAppInstance>();
				
				List<BluemixAppInfo> startedApps = appInfoLists.stream()
						//.filter(appInfo -> appInfo.getState().compareTo("STARTED") == 0)
				        .filter(appInfo -> appInfo.getState().compareTo("STARTED") == 0)
						.collect(Collectors.toList());
				
				log.info("center - {} appInstances - start", bluemixProperty.getCenter());
				for (BluemixAppInfo startedApp : startedApps) {
				    if(client != null)  {
    					List<ApplicationStatisticsResponse> responses = client.applicationsV2()
    							.statistics(ApplicationStatisticsRequest.builder().applicationId(startedApp.getId()).build())
    							.flux()
    							//.log()
    							.retry(3)
    							//.log()
    							.collectList()
    							.block();
    					
    					
    					responses.forEach(response -> {
    						Map<String, InstanceStatistics> instances = response.getInstances();
    						//log.info("app - {} / runningInstance - {} / instances - {}", startedApp.getApp(), startedApp.getRunningInstance(), instances.size());
                            
    						instances.forEach((key, stat) -> {
    							if(stat.getStatistics() == null)	{
    							    //log.info(msg);
    								return;
    							} else {
    								BluemixAppInstance appInstance = new BluemixAppInstance();
    								appInstance.setType(BLUEMIX_APP_INSTANCE_INFO);
    								appInstance.setCenter(bluemixProperty.getCenter());
    								appInstance.setOrg(startedApp.getOrg());
    								appInstance.setSpace(startedApp.getSpace());
    								appInstance.setApp(startedApp.getApp());
    								appInstance.setId(startedApp.getId());
    								appInstance.setIp(stat.getStatistics().getHost());
    								appInstance.setState(stat.getState());
    								appInstance.setIndex(key);
    								appInstance.setHost("");
    								appInstance.setUptime(stat.getStatistics().getUptime().longValue());
    								appInstance.setCpuUsage(stat.getStatistics().getUsage().getCpu());
    								
    								appInstance.setMemory(stat.getStatistics().getUsage().getMemory());
    								appInstance.setDisk(stat.getStatistics().getUsage().getDisk());
    								
    								appInstance.setMemoryQuota(stat.getStatistics().getMemoryQuota());
    								appInstance.setDiskQuota(stat.getStatistics().getDiskQuota());
    								
    								appInstance.setDiskUsage(((float) appInstance.getDisk() / appInstance.getDiskQuota()) * 100.0f); 
    								appInstance.setMemoryUsage(((float) appInstance.getMemory() / appInstance.getMemoryQuota()) * 100.0f);
    								appInstance.setTimeStamp(currentTime);
    								appInstances.add(appInstance);
    							}
    						});
    					});
				    } else {
				        log.info("### - client is null...");
				    }
				}
				log.info("center - {} appInstances - end / list size is - {}", bluemixProperty.getCenter() ,appInstances.size());
				
				sendListsToKafka(appInfoLists, kafkaTopic);
				sendListsToKafka(appInstances, kafkaTopic);
			}
		});
		
		
	}
		

	public void sendListsToKafka(List<?> list, String topic)	{
		list.forEach(data -> {
			try {
				System.out.println(data);
				kafkaTemplate.send(topic, objectMapper.writeValueAsString(data));
			} catch (JsonProcessingException jsonProcessingException) {
				log.error(jsonProcessingException.getMessage());
			}
		});
	}
	/*
	 * public static void main(String args[]) { List<String> testLists = new
	 * ArrayList<>(); testLists.add("test1"); testLists.add("test2");
	 * testLists.add("test3"); testLists.stream().map(str -> { return str; }). }
	 */
}
