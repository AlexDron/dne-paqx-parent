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
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.model.TaskResponse;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task responsible for renaming the host datastore after it has been added to vcenter.
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DatastoreRenameTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreRenameTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository dataServiceRepository;

    private static final String DATASTORE_PREFIX_NAME = "DAS";
    private static final String SERVER_NUMBER_REGEX   = "[^0-9]";
    private static final String HYPHEN_SPLITTER       = "-";

    /**
     * DatastoreRenameTaskHandler constructor.
     *
     * @param nodeService           - The <code>NodeService</code> instance.
     * @param dataServiceRepository
     * @since 1.0
     */
    public DatastoreRenameTaskHandler(NodeService nodeService, final DataServiceRepository dataServiceRepository)
    {
        this.nodeService = nodeService;
        this.dataServiceRepository = dataServiceRepository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute datastore rename task");

        final TaskResponse response = initializeResponse(job);

        try
        {
            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job input parameters are null");
            }

            final ComponentEndpointIds vCenterComponentEndpointIdsByEndpointType = dataServiceRepository
                    .getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (vCenterComponentEndpointIdsByEndpointType == null)
            {
                throw new IllegalStateException("VCenter Component Endpoint Ids are null");
            }

            final String esxiManagementHostname = inputParams.getEsxiManagementHostname();

            if (esxiManagementHostname == null)
            {
                throw new IllegalStateException("Hostname is null");
            }

            final String oldDatastoreName = "datastore1";
            final String newDatastoreName = buildDatastoreNewName(esxiManagementHostname);

            final DatastoreRenameRequestMessage requestMessage = new DatastoreRenameRequestMessage();
            requestMessage.setDatastoreName(oldDatastoreName);
            requestMessage.setNewDatastoreName(newDatastoreName);
            requestMessage.setCredentials(new Credentials(vCenterComponentEndpointIdsByEndpointType.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                    vCenterComponentEndpointIdsByEndpointType.getComponentUuid(),
                    vCenterComponentEndpointIdsByEndpointType.getEndpointUuid(),
                    vCenterComponentEndpointIdsByEndpointType.getCredentialUuid()));

            boolean succeeded = this.nodeService.requestDatastoreRename(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("Datastore rename failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception ex)
        {
            LOGGER.error("Error during datastore rename", ex);
            response.addError(ex.toString());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;
    }

    public static String buildDatastoreNewName(final String esxiManagementHostname)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append(DATASTORE_PREFIX_NAME);
        String serverNumber;

        if (esxiManagementHostname.contains(HYPHEN_SPLITTER))
        {
            serverNumber = esxiManagementHostname.substring(esxiManagementHostname.indexOf(HYPHEN_SPLITTER))
                    .replaceAll(SERVER_NUMBER_REGEX, "");
        }
        else
        {
            serverNumber = esxiManagementHostname.replaceAll(SERVER_NUMBER_REGEX, "");
        }

        builder.append(serverNumber);
        return builder.toString();
    }
}
