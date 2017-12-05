package com.skb.onm.bluemix.app.collector.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class BluemixAppInstance {
	
	private String type;
	
	/* TAG */
	private String center;
	private String org;
	private String space;
	private String host;
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
