/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;


/**
 * Task responsible for changing the idrac credentials.
 * 
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

import com.dell.cpsd.paqx.dne.service.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.springframework.util.StringUtils;

public class ChangeIdracCredentialsTaskHandler  extends BaseTaskHandler implements IWorkflowTaskHandler
{

    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeIdracCredentialsTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;
    
    /**
     * ChangeIdracCredentialsTaskHandler constructor.
     * 
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     * 
     * @since 1.0
     */
    public ChangeIdracCredentialsTaskHandler(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Execute ChangeIdracCredentialsTaskHandler task");

        TaskResponse response = initializeResponse(job);

        try
        {
            if (StringUtils.isEmpty(job.getInputParams().getNodeId()))
            {
                throw new IllegalStateException("No discovered node passed.");
            }

            ChangeIdracCredentialsResponse responseMessage = this.nodeService.changeIdracCredentials(job.getInputParams().getNodeId());
            
            if ("SUCCESS".equalsIgnoreCase(responseMessage.getMessage()))
            {
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error while changing the idrac credentials", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }
    
    /**
     * Create the <code>ChangeIdracCredentialsResponse</code> instance and initialize it.
     * 
     * @param job
     *            - The <code>Job</code> this task is part of.
     */
    @Override
    public ChangeIdracCredentialsResponse initializeResponse(Job job)
    {
        ChangeIdracCredentialsResponse response = new ChangeIdracCredentialsResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

}
