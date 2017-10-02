/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 */
package com.dell.cpsd.paqx.dne.service;

import com.dell.cpsd.paqx.dne.service.delegates.exception.JobNotFoundException;
import com.dell.cpsd.paqx.dne.service.model.multinode.Status;

import java.util.Map;

public interface ICamundaWorkflowService
{
    /**
     * Start workflow and return Job ID
     * @return
     */
    String startWorkflow(String processId, Map<String, Object> inputVariables);

    Status getStatus(String jobId) throws JobNotFoundException;
}
