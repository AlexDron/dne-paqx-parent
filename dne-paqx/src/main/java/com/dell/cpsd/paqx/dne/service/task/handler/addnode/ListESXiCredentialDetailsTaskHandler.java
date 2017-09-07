/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.ListESXiCredentialDetailsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
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
public class ListESXiCredentialDetailsTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListESXiCredentialDetailsTaskHandler.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    public ListESXiCredentialDetailsTaskHandler(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute List ESXi Credential Details task");

        final ListESXiCredentialDetailsTaskResponse response = initializeResponse(job);

        try
        {
            final ListEsxiCredentialDetailsRequestMessage requestMessage = getListDefaultCredentialsRequestMessage();

            final ComponentEndpointIds returnData = this.nodeService.listDefaultCredentials(requestMessage);

            if (returnData == null)
            {
                throw new IllegalStateException("List default credentials failed");
            }

            response.setComponentUuid(returnData.getComponentUuid());
            response.setEndpointUuid(returnData.getEndpointUuid());
            response.setCredentialUuid(returnData.getCredentialUuid());

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Error listing ESXi credential details", e);
            response.addError(e.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    private ListEsxiCredentialDetailsRequestMessage getListDefaultCredentialsRequestMessage()
    {
        final ListEsxiCredentialDetailsRequestMessage requestMessage = new ListEsxiCredentialDetailsRequestMessage();
        requestMessage.setComponentElementType(ListEsxiCredentialDetailsRequestMessage.ComponentElementType.COMMON_SERVER);
        requestMessage
                .setEndpointElementType(ListEsxiCredentialDetailsRequestMessage.EndpointElementType.COMMON_DELL_POWEREDGE_ESXI_HOST_EP);
        requestMessage.setCredentialName(ListEsxiCredentialDetailsRequestMessage.CredentialName.ESXI_HOST_DEFAULT);

        return requestMessage;
    }

    @Override
    public ListESXiCredentialDetailsTaskResponse initializeResponse(Job job)
    {
        final ListESXiCredentialDetailsTaskResponse response = new ListESXiCredentialDetailsTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }
}