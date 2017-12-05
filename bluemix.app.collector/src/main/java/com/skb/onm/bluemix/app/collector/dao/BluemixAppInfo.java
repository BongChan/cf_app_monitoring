package com.skb.onm.bluemix.app.collector.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BluemixAppInfo {

	private String type;
	
	/* TAG */
	private String center;
	private String org;
	private String space;
	private String app;
	private String id;

	/* Field */
	private String state;
	private int instances;
	private int runningInstance;
	private long memoryQuota;
	private long diskQuota;

	/* TIMESTAMP */
	private long timeStamp;
}
