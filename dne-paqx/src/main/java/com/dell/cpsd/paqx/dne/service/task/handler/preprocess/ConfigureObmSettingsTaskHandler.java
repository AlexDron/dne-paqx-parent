/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.preprocess;

import com.dell.cpsd.ObmConfig;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ObmSettingsResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Task responsible for configuring obm settings.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class ConfigureObmSettingsTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureObmSettingsTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     * The obm services to set
     */
    private final String[] obmServices;

    /**
     *  ConfigureObmSettingsTaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    public ConfigureObmSettingsTaskHandler(NodeService nodeService, String[] obmServices)
    {
        this.nodeService = nodeService;
        this.obmServices = obmServices;
    }

    /**
     * Perform the task of setting up obm settings.
     *
     * @param job
     *            - The <code>Job</code> this task is part of.
     *
     * @since 1.0
     */
    @Override
    public boolean executeTask(Job job)
    {
        LOGGER.info("Configuring Obm Settings Task");
        TaskResponse response = initializeResponse(job);

        try
        {

            String uuid = job.getInputParams().getSymphonyUuid();
            String ipAddress = job.getInputParams().getIdracIpAddress();

            LOGGER.info("uuid: " + uuid);
            LOGGER.info("ipAddress: " + ipAddress);
            LOGGER.info("obmServices: " + Arrays.toString(this.obmServices));

            SetObmSettingsRequestMessage configureObmSettingsRequest = new SetObmSettingsRequestMessage();
            configureObmSettingsRequest.setServices(Arrays.asList(obmServices));
            configureObmSettingsRequest.setUuid(uuid);

            ObmConfig obmConfig = new ObmConfig();
            obmConfig.setHost(ipAddress);
            configureObmSettingsRequest.setObmConfig(obmConfig);

            ObmSettingsResponse obmSettingsResponse = nodeService.obmSettingsResponse(configureObmSettingsRequest);
            if ("SUCCESS".equalsIgnoreCase(obmSettingsResponse.getStatus()))
            {
                response.setResults(buildResponseResult(obmSettingsResponse));
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
            else
            {
                response.addError(obmSettingsResponse.getErrors().toString());
            }
        }
        catch(Exception e)
        {
            LOGGER.error("Error showing obm settings", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(ObmSettingsResponse obmSettingsResponse)
    {
        Map<String, String> result = new HashMap<>();

        if (obmSettingsResponse == null)
        {
            return result;
        }

        if (obmSettingsResponse.getStatus() != null)
        {
            result.put("obmSettingsResponseStatus", obmSettingsResponse.getStatus());
        }

        if (obmSettingsResponse.getErrors() != null)
        {
            result.put("obmSettingsResponseErrorList", obmSettingsResponse.getErrors().toString());
        }

        return result;
    }

    /**
     * Create the <code>ObmSettingsResponse</code> instance and initialize it.
     *
     * @param job
     *            - The <code>Job</code> this task is part of.
     */
    @Override
    public ObmSettingsResponse initializeResponse(Job job)
    {
        ObmSettingsResponse response = new ObmSettingsResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);
        return response;
    }

}
