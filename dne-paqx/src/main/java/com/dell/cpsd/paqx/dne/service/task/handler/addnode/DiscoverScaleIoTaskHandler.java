package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DiscoverScaleIoTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ListScaleIoComponentsTaskResponse;
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
public class DiscoverScaleIoTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverScaleIoTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    public DiscoverScaleIoTaskHandler(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Discover ScaleIO task");

        final TaskResponse response = initializeResponse(job);

        try
        {
            final Map<String, TaskResponse> responseMap = job.getTaskResponseMap();
            final ListScaleIoComponentsTaskResponse listScaleIoComponents = (ListScaleIoComponentsTaskResponse)responseMap.get("listScaleIoComponents");
            if (listScaleIoComponents == null)
            {
                throw new IllegalStateException("No list scale io components task found.");
            }

            final ComponentEndpointIds componentEndpointIds = listScaleIoComponents.getComponentEndpointIds();

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No ScaleIO components found.");
            }

            final boolean success = this.nodeService.requestDiscoverScaleIo(componentEndpointIds, job.getId().toString());

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
    public DiscoverScaleIoTaskResponse initializeResponse(Job job)
    {
        final DiscoverScaleIoTaskResponse response = new DiscoverScaleIoTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }
}
