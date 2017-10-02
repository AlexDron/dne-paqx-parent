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
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.MaintenanceModeRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHostMaintenanceMode extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHostMaintenanceMode.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    /*
    * The task name
    */
    private final String taskName;

    /**
     * AbstractHostMaintenanceModeTaskHandler constructor
     *
     * @param nodeService - The <code>NodeService</code> instance
     * @param repository  - The <code>DataServiceRepository</code> instance
     * @param taskName    - The task name
     * @since 1.0
     */
    public AbstractHostMaintenanceMode(final NodeService nodeService, final DataServiceRepository repository,
                                       final String taskName)
    {
        this.nodeService = nodeService;
        this.repository = repository;
        this.taskName = taskName;
    }

    /**
     * Subclasses hould override to set the desired maintenance mode state.
     *
     * @return True to enter maintenance mode, false to exit maintenance mode
     * @since 1.0
     */
    protected abstract boolean getMaintenanceModeEnable();

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute {}", this.taskName);

       /* final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType(
                "VCENTER-CUSTOMER");
        final String hostname = (String) delegateExecution.getVariable(HOSTNAME);

        final boolean maintenanceModeEnable = this.getMaintenanceModeEnable();
        final HostMaintenanceModeRequestMessage requestMessage = getHostMaintenanceModeRequestMessage(
                componentEndpointIds, hostname, maintenanceModeEnable);
        final boolean success = this.nodeService.requestHostMaintenanceMode(requestMessage);
        if (!success)
        {
            LOGGER.error(taskName + " failed!");
            updateDelegateStatus(taskName + " failed!");
            throw new BpmnError(INSTALL_ESXI_FAILED, taskName + " failed!");
        }
*/
        LOGGER.info(taskName + " was successful.");
        updateDelegateStatus(taskName + " was successful.");
    }

    protected HostMaintenanceModeRequestMessage getHostMaintenanceModeRequestMessage(
            final ComponentEndpointIds componentEndpointIds, final String hostname, final boolean maintenanceModeEnable)
    {
        final HostMaintenanceModeRequestMessage requestMessage = new HostMaintenanceModeRequestMessage();
        final MaintenanceModeRequest maintenanceModeRequest = new MaintenanceModeRequest();
        maintenanceModeRequest.setMaintenanceModeEnable(maintenanceModeEnable);
        maintenanceModeRequest.setHostName(hostname);
        requestMessage.setMaintenanceModeRequest(maintenanceModeRequest);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(
                componentEndpointIds.getComponentUuid(), componentEndpointIds.getEndpointUuid(),
                componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }
}
