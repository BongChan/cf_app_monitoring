/*******************************************************************************
 *
 * Copyright (c) 2017 SK C&C Co., Ltd. All rights reserved.
 *
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 *******************************************************************************/

package com.skb.onm.bluemix.app.consumer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skb.onm.bluemix.app.consumer.dao.BluemixAppInfo;
import com.skb.onm.bluemix.app.consumer.dao.BluemixAppInstance;
import com.skb.onm.bluemix.app.consumer.dao.VmwareResource;

import lombok.extern.slf4j.Slf4j;

/**
 * <ul>
 * <li>업무 그룹명 : onm-consumer-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.bluemix.consumer</li>
 * <li>설  명 : AnnotationKafkaListener</li>
 * <li>작성일 : 2017. 10. 25.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */

@Slf4j
@Component
public class AnnotationKafkaListener {
	
	/**
	 * influxDB 
	 */
	@Autowired
	private InfluxDB influxDB;

	/**
	 * objectMapper
	 */
	@Autowired
	private ObjectMapper objectMapper;

	private DecimalFormat decimalFormat;

	private Map<String, String> hostMap;
	
	@PostConstruct
	public void init() {
		this.decimalFormat = new DecimalFormat("#.####");
		hostMap = new HashMap<>();
	}

	/**
	 * listener
	 *  
	 * @param data
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException void
	 */
	
	//check multi topics
	@KafkaListener(id = "bluemix-consumer-app", topics = "${bluemix.kafka.topics}")
	public void listener(String data) throws JsonParseException, JsonMappingException, IOException	{
		String upperData = StringUtils.upperCase(data);
		
		Point point = null;
		//Object jsonObject = readValueByObjectMapper(data);
		//if(jsonObject instanceof BluemixAppInfo)	{
		if(StringUtils.contains(upperData, "BLUEMIXAPPINFO"))	{
			//BluemixAppInfo bluemixAppInfo = (BluemixAppInfo) jsonObject;
			BluemixAppInfo bluemixAppInfo = objectMapper.readValue(data, BluemixAppInfo.class);
			point = Point.measurement("bluemix_app_info")
						.time(bluemixAppInfo.getTimeStamp(), TimeUnit.MILLISECONDS)
						.tag("center", bluemixAppInfo.getCenter())
						.tag("org", bluemixAppInfo.getOrg())
						.tag("space", bluemixAppInfo.getSpace())
						.tag("app", bluemixAppInfo.getApp())
						.tag("id", bluemixAppInfo.getId())
						.addField("sourceId", bluemixAppInfo.getCenter() 
						    + ":" + bluemixAppInfo.getOrg() 
						    + ":" + bluemixAppInfo.getSpace() 
						    + ":" + bluemixAppInfo.getApp() 
						    + ":" + bluemixAppInfo.getId())
						.addField("state", bluemixAppInfo.getState())
						.addField("instances", bluemixAppInfo.getInstances())
						.addField("runningInstance", bluemixAppInfo.getRunningInstance())
						.addField("memoryQuota", bluemixAppInfo.getMemoryQuota())
						.addField("diskQuota", bluemixAppInfo.getDiskQuota())
						.build();
		
		//} else if(jsonObject instanceof BluemixAppInstance)	{
		} else if(StringUtils.contains(upperData, "BLUEMIXAPPINSTANCEINFO"))	{
			//BluemixAppInstance bluemixAppInstance = (BluemixAppInstance) jsonObject;
			BluemixAppInstance bluemixAppInstance = objectMapper.readValue(data, BluemixAppInstance.class);
			String ip = bluemixAppInstance.getIp();
			//get host from map - hostMap
			String host = "";
			if(ip != null)	{
				host = getHost(ip);
			} else {
				ip = "";
			}
			point = Point.measurement("bluemix_app_instance_info")
						.time(bluemixAppInstance.getTimeStamp(), TimeUnit.MILLISECONDS)
						.tag("center", bluemixAppInstance.getCenter())
						.tag("org", bluemixAppInstance.getOrg())
						.tag("space", bluemixAppInstance.getSpace())
						.tag("host", host)
						.tag("ip", ip)
						.tag("app", bluemixAppInstance.getApp())
						.tag("id", bluemixAppInstance.getId())
						.tag("index", bluemixAppInstance.getIndex())
						.addField("sourceId", bluemixAppInstance.getCenter()
						    + ":" + bluemixAppInstance.getOrg()
						    + ":" + bluemixAppInstance.getSpace()
						    + ":" + bluemixAppInstance.getHost()
						    + ":" + bluemixAppInstance.getIp()
						    + ":" + bluemixAppInstance.getIndex())
						.addField("state", bluemixAppInstance.getState())
						.addField("cpuUsage", Double.parseDouble(decimalFormat.format(bluemixAppInstance.getCpuUsage())))
						.addField("memory", bluemixAppInstance.getMemory())
						.addField("memoryQuota", bluemixAppInstance.getMemoryQuota())
						.addField("memoryUsage", Double.parseDouble(decimalFormat.format(bluemixAppInstance.getMemoryUsage())))
						.addField("disk", bluemixAppInstance.getDisk())
						.addField("diskQuota", bluemixAppInstance.getDiskQuota())
						.addField("diskUsage", Double.parseDouble(decimalFormat.format(bluemixAppInstance.getDiskUsage())))
						.build();
		} else {
			log.info("this code isn't not working");
		}
		
		//objectMapper.readValue(data, BluemixAppInfo.class);
		if(point != null) {
			influxDB.write(point);
		} else {
			log.info("point is null... this is not good");
		}
	}
	
	public String getHost(String ip)	{
		String host = "";
		if(hostMap.containsKey(ip))	{
			host = hostMap.get(ip);
			return host;
		} else {
			log.info("select " + ip);
			Query query = new Query("select * from vmware_vm_resource where ip = '" + ip
					+ "' order by time desc limit 1", "vmware");
			QueryResult queryResult = influxDB.query(query);
			InfluxDBResultMapper resultMapper = new InfluxDBResultMapper();
			List<VmwareResource> vmwareResources = resultMapper.toPOJO(queryResult, VmwareResource.class);
			log.info("vmwareResource size - {}", vmwareResources.size());
			if(vmwareResources != null && vmwareResources.size() > 0)	{
				VmwareResource vmwareResource = vmwareResources.get(0);
				if(vmwareResource.getVmNameAlias() != null && !vmwareResource.getVmNameAlias().equals(""))	{
					host = vmwareResource.getVmNameAlias();
				} else if(vmwareResource.getVmName() != null && !vmwareResource.getVmName().equals(""))	{
					host = vmwareResource.getVmName();
				} else {
					log.info("no matched ip host vmnamealias or vmname -> set host to ip (ip == host)");
					host = ip;
				}
			}
			hostMap.put(ip, host);
			return host;
		}
	}
	
	public Object readValueByObjectMapper(String value) {

		try {
			return objectMapper.readValue(value, BluemixAppInfo.class);
		} catch (JsonParseException e) {
			try {
				return objectMapper.readValue(value, BluemixAppInstance.class);
			} catch (JsonParseException jsonParseException) {
				log.error(jsonParseException.getMessage());
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	/*public String getSourceId(Class<?> clazz, Object vms) {
		SourceId sourceId = clazz.getAnnotation(SourceId.class);
		String[] sourceIds = sourceId.value();
		String field = "";
		String retStr = "";
		try {
			if (sourceIds == null || sourceIds.length == 0) {
				return retStr;
			} else {
				field = sourceIds[0];
				Method getMethod = clazz.getMethod("get" + WordUtils.capitalize(field), null);
				retStr = (String) getMethod.invoke(vms, null);
				for (int i = 1; i < sourceIds.length; i++) {
					field = sourceIds[i];
					getMethod = clazz.getMethod("get" + WordUtils.capitalize(field), null);
					retStr += ":" + getMethod.invoke(vms, null).toString();
				}
			}
		} catch(NoSuchMethodException noSuchMethodException)    {
		    log.error(noSuchMethodException.getMessage());
		} catch(SecurityException securityException)  {
		    log.error(securityException.getMessage());
		} catch (IllegalAccessException illegalAccessException) {
		    log.error(illegalAccessException.getMessage());
		} catch (IllegalArgumentException illegalArgumentException) {
		    log.error(illegalArgumentException.getMessage());
        } catch (InvocationTargetException invocationTargetException) {
            log.error(invocationTargetException.getMessage());
        } 

		return retStr;
	}*/

}
