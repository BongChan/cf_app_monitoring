/* 
 * Copyright (c) 2017. 11. 7. SK C&C Co., Ltd. All rights reserved.
 * 
 * This software is the confidential and proprietary information of SK C&C.
 * You shall not disclose such confidential information and shall use it
 * only in accordance with the terms of the license agreement you entered into
 * with SK C&C.
 */
package com.skb.onm.bluemix.app.collector.dao;

import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.operations.applications.ApplicationSummary;
import org.cloudfoundry.operations.organizations.OrganizationSummary;
import org.cloudfoundry.operations.spaces.SpaceSummary;

import reactor.core.publisher.Flux;

/**
 * <ul>
 * <li>업무 그룹명 : onm-collector-bluemix</li>
 * <li>서브 업무명 : com.skb.onm.bluemix.collector.dao</li>
 * <li>설  명 : BluemixCollectors</li>
 * <li>작성일 : 2017. 11. 7.</li>
 * <li>작성자 : ch.j</li>
 * </ul>
 */
public class BluemixCollectors {

    public static Flux<OrganizationSummary> getOrganizations(DefaultCloudFoundryOperations op) {
        return op.organizations().list();
    }

    public static Flux<SpaceSummary> getSpaces(DefaultCloudFoundryOperations op, String orgName) {
        return DefaultCloudFoundryOperations.builder()
                .from(op)
                .organization(orgName)
                .build()
                .spaces()
                .list();
    }

    public static Flux<ApplicationSummary> getApplicationSummaries(DefaultCloudFoundryOperations op, String orgName, String spaceName) {
        return DefaultCloudFoundryOperations.builder()
                .from(op)
                .organization(orgName).space(spaceName)
                .build()
                .applications()
                .list();
    }
}
