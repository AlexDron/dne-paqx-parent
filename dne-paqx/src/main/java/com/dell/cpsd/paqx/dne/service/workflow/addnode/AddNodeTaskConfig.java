/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.addnode;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dell.cpsd.paqx.dne.service.model.Step;

@Configuration
public class AddNodeTaskConfig
{
    @Bean("addNodeWorkflowSteps")
    public Map<String, Step> addNodeWorkflowSteps()
    {
        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startAddNodeWorkflow", new Step("retrieveEsxiDefaultCredentialDetails"));
        workflowSteps.put("retrieveEsxiDefaultCredentialDetails", new Step("changeIdracCredentials"));
        workflowSteps.put("changeIdracCredentials", new Step("esxi-credential-details"));
        workflowSteps.put("esxi-credential-details", new Step("updateSystemDefinition"));
        //TODO: Add all the steps here once tested
        workflowSteps.put("updateSystemDefinition", new Step("notifyNodeDiscoveryToUpdateStatus"));
        workflowSteps.put("notifyNodeDiscoveryToUpdateStatus", new Step("completed", true));
        workflowSteps.put("completed", null);

        return workflowSteps;
    }
}
