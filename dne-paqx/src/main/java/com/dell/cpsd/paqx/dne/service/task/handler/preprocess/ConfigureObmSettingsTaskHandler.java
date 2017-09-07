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
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
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

public class ConfigureObmSettingsTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler {

    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureObmSettingsTaskHandler.class);


    @Value("${obm.service.name}")
    private String serviceName = "dell-wsman-obm-service";


    /**
     * The <code>NodeService</code> instance
     */
    private NodeService nodeService;

    /**
     *  ConfigureObmSettingsTaskHandler constructor.
     *
     * @param nodeService
     *            - The <code>NodeService</code> instance.
     *
     * @since 1.0
     */
    public ConfigureObmSettingsTaskHandler(NodeService nodeService){
        this.nodeService = nodeService;
    }

    /**
     * Perform the task of setting up boot order and disabling PXE
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
            Map<String, TaskResponse> responseMap = job.getTaskResponseMap();
            TaskResponse findNodeTask = responseMap.get("findAvailableNodes");
            if (findNodeTask == null)
            {
                throw new IllegalStateException("No discovered node task found.");
            }
            if (findNodeTask.getResults() == null)
            {
                throw new IllegalStateException("No results found.");
            }

            if (findNodeTask.getResults().get("symphonyUUID") == null)
            {
                throw new IllegalStateException("No discovered node info found.");
            }

            job.getInputParams().setServiceName(serviceName);

            String uuid = findNodeTask.getResults().get("symphonyUUID");
            String ipAddress = job.getInputParams().getIdracIpAddress();
            String service = job.getInputParams().getServiceName();

            LOGGER.info("uuid:" + uuid);
            LOGGER.info("ipAddress:" + ipAddress);
            LOGGER.info("serviceName1:" + serviceName);


            SetObmSettingsRequestMessage configureObmSettingsRequest = new SetObmSettingsRequestMessage();
            configureObmSettingsRequest.setService(service);
            configureObmSettingsRequest.setUuid(uuid);

            ObmConfig obmConfig = new ObmConfig();
            obmConfig.setHost(ipAddress);
            configureObmSettingsRequest.setObmConfig(obmConfig);

            BootDeviceIdracStatus bootDeviceIdracStatus = nodeService.bootDeviceIdracStatus(configureObmSettingsRequest);
            if ("SUCCESS".equalsIgnoreCase(bootDeviceIdracStatus.getStatus()))
            {
                response.setResults(buildResponseResult(bootDeviceIdracStatus));
                response.setWorkFlowTaskStatus(Status.SUCCEEDED);
                return true;
            }
            else{
                response.addError(bootDeviceIdracStatus.getErrors().toString());
            }
        }
        catch(Exception e){
            LOGGER.error("Error showing boot order status", e);
            response.addError(e.toString());
        }
        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    /*
     * This method add all the node information to the response object
     */
    private Map<String, String> buildResponseResult(BootDeviceIdracStatus bootDeviceIdracStatus)
    {
        Map<String, String> result = new HashMap<>();

        if (bootDeviceIdracStatus == null)
        {
            return result;
        }

        if (bootDeviceIdracStatus.getStatus() != null)
        {
            result.put("bootDeviceIdracStatus", bootDeviceIdracStatus.getStatus());
        }

        if (bootDeviceIdracStatus.getErrors() != null)
        {
            result.put("bootDeviceIdracErrorsList", bootDeviceIdracStatus.getErrors().toString());
        }

        return result;
    }

    /**
     * Create the <code>BootOrderSequenceResponse</code> instance and initialize it.
     *
     * @param job
     *            - The <code>Job</code> this task is part of.
     */
    @Override
    public BootDeviceIdracStatus initializeResponse(Job job)
    {
        BootDeviceIdracStatus response = new BootDeviceIdracStatus();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);
        return response;
    }

}
