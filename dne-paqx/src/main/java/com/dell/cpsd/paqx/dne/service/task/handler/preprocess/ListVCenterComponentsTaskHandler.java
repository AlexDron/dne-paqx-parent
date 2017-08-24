/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ListVCenterComponentsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Document Usage
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ListVCenterComponentsTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListVCenterComponentsTaskHandler.class);

    private static final String SUCCESS = "SUCCESS";

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    public ListVCenterComponentsTaskHandler(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute List VCenter Components task");

        final ListVCenterComponentsTaskResponse response = initializeResponse(job);

        try
        {
            final boolean succeeded = this.nodeService.requestVCenterComponents();

            if (!succeeded)
            {
                throw new IllegalStateException("Request for VCenter components failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;

        }
        catch (Exception e)
        {
            LOGGER.error("Error while listing the VCenter components", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /**
     * Create the <code>ListVCenterComponentsTaskResponse</code> instance and initialize it.
     *
     * @param job - The <code>Job</code> this task is part of.
     */
    @Override
    public ListVCenterComponentsTaskResponse initializeResponse(Job job)
    {
        final ListVCenterComponentsTaskResponse response = new ListVCenterComponentsTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }
}
