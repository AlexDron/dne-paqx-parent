package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoverVCenterTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ListVCenterComponentsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class DiscoverVCenterTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverVCenterTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    public DiscoverVCenterTaskHandler(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Discover VCenter task");

        final TaskResponse response = initializeResponse(job);

        try
        {
            final Map<String, TaskResponse> responseMap = job.getTaskResponseMap();
            final ListVCenterComponentsTaskResponse listVCenterComponents = (ListVCenterComponentsTaskResponse)responseMap.get("listVCenterComponents");
            if (listVCenterComponents == null)
            {
                throw new IllegalStateException("No list vcenter components task found.");
            }

            final ComponentEndpointIds componentEndpointIds = listVCenterComponents.getComponentEndpointIds();

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final boolean success = this.nodeService.requestDiscoverVCenter(componentEndpointIds, job.getId().toString());

            response.setWorkFlowTaskStatus(success? Status.SUCCEEDED : Status.FAILED);

            return success;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            return false;
        }
    }

    @Override
    public DiscoverVCenterTaskResponse initializeResponse(Job job)
    {
        final DiscoverVCenterTaskResponse response = new DiscoverVCenterTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }
}
