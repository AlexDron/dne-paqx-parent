/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.workflow.preprocess;

import com.dell.cpsd.paqx.dne.service.model.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

@Configuration
public class PreProcessTaskConfig {
    @Bean("preProcessWorkflowSteps")
    public Map<String, Step> preProcessWorkflowSteps(){
        final Map<String, Step> workflowSteps = new HashMap<>();

        workflowSteps.put("startPreProcessWorkflow", new Step("findAvailableNodes"));
        workflowSteps.put("findAvailableNodes", new Step("listScaleIoComponents"));
        workflowSteps.put("listScaleIoComponents", new Step("listVCenterComponents"));
        workflowSteps.put("listVCenterComponents", new Step("discoverVCenter"));
        //TODO: Re-enable the discover scaleio when mdm is up and running
//        workflowSteps.put("discoverScaleIo", new Step("discoverVCenter"));
        workflowSteps.put("discoverVCenter", new Step("configIdrac"));
        workflowSteps.put("configIdrac", new Step("pingIdrac"));
        // after testing, move it to addNode workflow
        workflowSteps.put("pingIdrac", new Step("configureBootDeviceIdrac"));
        workflowSteps.put("configureBootDeviceIdrac", new Step("configurePxeBoot"));
        //TODO: Re-enable the find scaleio when mdm is up and running
        //workflowSteps.put("findScaleIO", new Step("findVCluster"));
        workflowSteps.put("configurePxeBoot",new Step("findVCluster"));
        workflowSteps.put("findVCluster", new Step("findProtectionDomain"));
        workflowSteps.put("findProtectionDomain", new Step("findSystemData"));
        workflowSteps.put("findSystemData", new Step("assignDefaultHostName"));
        workflowSteps.put("assignDefaultHostName", new Step("assignDefaultCredentials"));
        workflowSteps.put("assignDefaultCredentials", new Step("completed", true));
        workflowSteps.put("completed", null);

        return workflowSteps;
    }
}
