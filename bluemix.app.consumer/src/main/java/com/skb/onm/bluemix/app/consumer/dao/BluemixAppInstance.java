package com.skb.onm.bluemix.app.consumer.dao;

import lombok.Data;

@Data
public class BluemixAppInstance {
	
	private String type;
	
	/* TAG */
	private String center;
	private String org;
	private String space;
	private String host; // <- Influx 호출 후 IP에 해당하는 vmNameAlias로 설정 필요(vmNameAlias -> vmName -> ip)
	private String ip;
	private String app;
	private String id;
	private String index;

	/* Field */
	private String state;
	private double cpuUsage; /* % */
	private long memory;
	private long memoryQuota;
	private float memoryUsage; /* % */
	private long disk;
	private long diskQuota;
	private float diskUsage; /* % */
	private long uptime;

	/* TIMESTAMP */
	private long timeStamp;
}
