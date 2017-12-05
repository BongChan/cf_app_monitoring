package com.skb.onm.bluemix.app.collector.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluemix.collector")
public class BluemixProperties {
    private List<BluemixProperty> bluemixProperties = null;
}
