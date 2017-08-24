/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.WorkflowService;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.  Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class FindSystemDataTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindSystemDataTaskHandler.class);

    private WorkflowService workflowService;

    public FindSystemDataTaskHandler(WorkflowService workflowService){
        this.workflowService = workflowService;
    }

    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute SystemData task");
        TaskResponse response = initializeResponse(job);
        try {
            Thread.sleep(50000);
        }
        catch(Exception e){}
        response.setWorkFlowTaskStatus(Status.SUCCEEDED);
        return true;
    }

}
