package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.paqx.dne.transformers.HostToInstallEsxiRequestTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class InstallEsxiTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstallEsxiTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService                         nodeService;
    private final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer;

    public InstallEsxiTaskHandler(final NodeService nodeService, final HostToInstallEsxiRequestTransformer hostToInstallEsxiRequestTransformer)
    {
        this.nodeService = nodeService;
        this.hostToInstallEsxiRequestTransformer = hostToInstallEsxiRequestTransformer;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Install ESXi task");

        final InstallEsxiTaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final String nodeId = inputParams.getComponentUuid();

            if (StringUtils.isEmpty(nodeId))
            {
                throw new IllegalStateException("Node Id is null");
            }

            final String esxiManagementIpAddress = inputParams.getEsxiManagementIpAddress();

            if (StringUtils.isEmpty(esxiManagementIpAddress))
            {
                throw new IllegalStateException("ESXi Management IP Address is null");
            }

            String esxiManagementHostname = inputParams.getEsxiManagementHostname();

            if (StringUtils.isEmpty(esxiManagementHostname))
            {
                LOGGER.warn("ESXi Management hostname is null, will auto generate hostname");

                esxiManagementHostname = this.generateHostname(esxiManagementIpAddress);

                LOGGER.info("Auto generated ESXi Management hostname is " + esxiManagementHostname);
            }

            response.setHostname(esxiManagementHostname);

            final EsxiInstallationInfo esxiInstallationInfo = hostToInstallEsxiRequestTransformer
                    .transformInstallEsxiData(esxiManagementHostname, nodeId);

            final boolean success = this.nodeService.requestInstallEsxi(esxiInstallationInfo);

            response.setWorkFlowTaskStatus(success? Status.SUCCEEDED : Status.FAILED);
            return success;

        }
        catch (Exception e)
        {
            LOGGER.error("Error installing ESXi", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    @Override
    public InstallEsxiTaskResponse initializeResponse(Job job)
    {
        final InstallEsxiTaskResponse response = new InstallEsxiTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    /*
    * Auto generates the hostname using the ESXI Management IP Address.
    */
    public String generateHostname(final String esxiManagementIpAddress)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("vCenterHost");
        builder.append("_");
        builder.append(esxiManagementIpAddress.replaceAll("\\.", "_"));
        return builder.toString();
    }

}
