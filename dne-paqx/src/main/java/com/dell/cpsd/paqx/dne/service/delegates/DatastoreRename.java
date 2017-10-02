/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DatastoreRenameRequestMessage;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.DATASTORE_NAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.HOSTNAME;
import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.INSTALL_ESXI_FAILED;

@Component
@Scope("prototype")
@Qualifier("datastoreRename")
public class DatastoreRename extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatastoreRename.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
    * The <code>DataServiceRepository</code> instance
    */
    private final DataServiceRepository dataServiceRepository;

    private static final String DATASTORE_PREFIX_NAME = "DAS";
    private static final String SERVER_NUMBER_REGEX = "[^0-9]";
    private static final String HYPHEN_SPLITTER = "-";

    @Autowired
    public DatastoreRename(final NodeService nodeService, final DataServiceRepository dataServiceRepository)
    {
        this.nodeService = nodeService;
        this.dataServiceRepository = dataServiceRepository;
    }

    public static String buildDatastoreNewName(final String esxiManagementHostname)
    {
        final StringBuilder builder = new StringBuilder();
        builder.append(DATASTORE_PREFIX_NAME);
        String serverNumber;

        if (esxiManagementHostname.contains(HYPHEN_SPLITTER))
        {
            serverNumber = esxiManagementHostname.substring(esxiManagementHostname.indexOf(HYPHEN_SPLITTER)).replaceAll(
                    SERVER_NUMBER_REGEX, "");
        }
        else
        {
            serverNumber = esxiManagementHostname.replaceAll(SERVER_NUMBER_REGEX, "");
        }

        builder.append(serverNumber);
        return builder.toString();
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute datastore rename");

        final String esxiManagementHostname = (String) delegateExecution.getVariable(HOSTNAME);

        final ComponentEndpointIds vCenterComponentEndpointIdsByEndpointType = dataServiceRepository
                .getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

        final String newDatastoreName = buildDatastoreNewName(esxiManagementHostname);

        final DatastoreRenameRequestMessage requestMessage = new DatastoreRenameRequestMessage();
        requestMessage.setHostname(esxiManagementHostname);
        requestMessage.setNewDatastoreName(newDatastoreName);
        requestMessage.setCredentials(new Credentials(vCenterComponentEndpointIdsByEndpointType.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                vCenterComponentEndpointIdsByEndpointType.getComponentUuid(),
                vCenterComponentEndpointIdsByEndpointType.getEndpointUuid(),
                vCenterComponentEndpointIdsByEndpointType.getCredentialUuid()));

        boolean succeeded;
        try
        {
            succeeded = this.nodeService.requestDatastoreRename(requestMessage);
        }
        catch (Exception ex)
        {
            LOGGER.error("An Unexpected Exception Occurred attempting to Rename the Datastore.", ex);
            updateDelegateStatus("An Unexpected Exception Occurred attempting to Rename the Datastore.");
            updateWorkflowErrorResult(true, delegateExecution);
            throw new BpmnError(INSTALL_ESXI_FAILED,"An Unexpected Exception Occurred attempting to Rename the Datastore.  Reason: " + ex.getMessage());
        }
        if (!succeeded)
        {
            LOGGER.error("Datastore Rename failed!");
            updateDelegateStatus(
                    "Datastore Rename failed!");
            updateWorkflowErrorResult(true, delegateExecution);
            throw new BpmnError(INSTALL_ESXI_FAILED,
                                "Datastore Rename failed!");
        }
        LOGGER.info("Datastore Rename was successful.");
        updateDelegateStatus("Datastore Rename was successful.");
        delegateExecution.setVariable(DATASTORE_NAME, newDatastoreName);
    }
}
