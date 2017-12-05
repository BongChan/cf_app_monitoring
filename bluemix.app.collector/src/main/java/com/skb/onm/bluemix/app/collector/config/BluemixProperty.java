/* 
 * Copyright (c) 2017. 11. 7. SK C&C Co., Ltd. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 */
package com.skb.onm.bluemix.app.collector.config;

import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;

import lombok.Data;

/**
 * <ul>
 * <li>업무 그룹명 : onm-collector-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.collector.config</li>
 * <li>설  명 : BluemixProperty</li>
 * <li>작성일 : 2017. 11. 7.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */
@Data
public class BluemixProperty {
    private String center;
    private String ip;
    //private String subscriptionId;
    private String user;
    private String password;
    private Boolean skipSslValidation;
    private ReactorCloudFoundryClient reactorCloudFoundryClient;
}
