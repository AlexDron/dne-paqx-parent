/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ApplyEsxiLicenseTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
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
public class ApplyEsxiLicenseTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyEsxiLicenseTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public ApplyEsxiLicenseTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Apply Esxi License task");

        final ApplyEsxiLicenseTaskResponse response = initializeResponse(job);

        try
        {
            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final String hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final AddEsxiHostVSphereLicenseRequest requestMessage = getLicenseRequest(componentEndpointIds, hostname);

            final boolean success = this.nodeService.requestInstallEsxiLicense(requestMessage);

            if (!success)
            {
                // Don't want to fail the workflow for this task handler because ESXi
                // comes with an evaluation license for 60 days so no need to fail it.
                final String msg = "ESXi license application failed. This step needs to be performed manually";
                LOGGER.warn(msg);
                response.addWarning(msg);
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);

            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);

        return false;
    }

    private AddEsxiHostVSphereLicenseRequest getLicenseRequest(final ComponentEndpointIds componentEndpointIds, final String hostname)
    {
        final AddEsxiHostVSphereLicenseRequest requestMessage = new AddEsxiHostVSphereLicenseRequest();
        requestMessage.setHostname(hostname);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    @Override
    public ApplyEsxiLicenseTaskResponse initializeResponse(Job job)
    {
        final ApplyEsxiLicenseTaskResponse response = new ApplyEsxiLicenseTaskResponse();
        setupResponse(job,response);
        return response;
    }
}