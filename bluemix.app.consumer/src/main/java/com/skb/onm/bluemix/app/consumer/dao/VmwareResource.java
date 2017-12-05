package com.skb.onm.bluemix.app.consumer.dao;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import lombok.Data;

@Data
@Measurement(name = "vmware_vm_resource")
public class VmwareResource {

	@Column(name = "time")
	private Instant time;
	
	@Column(name = "vmName", tag = true)
	private String vmName;
	
	@Column(name = "vmNameAlias", tag = true)
	private String vmNameAlias;
}
