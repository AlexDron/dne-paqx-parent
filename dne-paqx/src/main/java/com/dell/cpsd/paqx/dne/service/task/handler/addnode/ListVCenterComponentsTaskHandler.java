package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import com.dell.cpsd.paqx.dne.service.model.EndpointCredentials;
import com.dell.cpsd.paqx.dne.service.model.ListVCenterComponentsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
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

        final TaskResponse response = initializeResponse(job);

        try
        {
            final ListVCenterComponentsTaskResponse taskResponse = this.nodeService.requestVCenterComponents();

            if (taskResponse == null)
            {
                response.setWorkFlowTaskStatus(Status.FAILED);
                return false;
            }

            if (SUCCESS.equalsIgnoreCase(taskResponse.getMessage()))
            {
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                response.setResults(buildResponseResult(taskResponse));
                return true;
            }
            else
            {
                response.setWorkFlowTaskStatus(Status.FAILED);
                return false;
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Error while listing the VCenter components", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(final ListVCenterComponentsTaskResponse response)
    {
        final Map<String, String> result = new HashMap<>();

        final List<ComponentEndpointDetails> componentEndpointDetailsList = response.getComponentEndpointDetails();

        if (componentEndpointDetailsList.isEmpty())
        {
            LOGGER.error("No Components Found");
            return null;
        }

        //For MVP Getting the first component - this can be selected from UI later on
        final ComponentEndpointDetails componentEndpointDetails = componentEndpointDetailsList.get(0);
        final List<EndpointCredentials> endpointCredentialList = componentEndpointDetails.getEndpointCredentials();

        if (endpointCredentialList.isEmpty())
        {
            LOGGER.error("No Endpoints Found");
            return null;
        }

        //For MVP Getting the first endpoint - this can be selected from UI later on
        final EndpointCredentials endpointCredentials = endpointCredentialList.get(0);
        final List<String> credentialUuids = endpointCredentials.getCredentialUuids();

        if (credentialUuids.isEmpty())
        {
            LOGGER.error("No Credentials Found");
            return null;
        }

        //For MVP Getting the first credential - this can be selected from UI later on
        final String credentialUuid = credentialUuids.get(0);
        final String endpointUuid = endpointCredentials.getEndpointUuid();
        final String endpointUrl = endpointCredentials.getEndpointUrl();
        final String componentUuid = componentEndpointDetails.getComponentUuid();

        if (credentialUuid != null)
        {
            result.put("vCenterCredentialUuid", credentialUuid);
        }

        if (endpointUuid != null)
        {
            result.put("vCenterEndpointUuid", endpointUuid);
        }

        if (endpointUrl != null)
        {
            result.put("vCenterEndpointUrl", endpointUrl);
        }

        if (componentUuid != null)
        {
            result.put("vCenterComponentUuid", componentUuid);
        }

        return result;
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
