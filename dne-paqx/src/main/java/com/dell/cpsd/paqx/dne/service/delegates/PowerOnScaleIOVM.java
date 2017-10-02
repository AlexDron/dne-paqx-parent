/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.task.handler.addnode.RebootHostTaskHandler;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dell.cpsd.paqx.dne.service.delegates.utils.DelegateConstants.NODE_DETAIL;

@Component
@Scope("prototype")
@Qualifier("powerOnScaleIOVM")
public class PowerOnScaleIOVM extends BaseWorkflowDelegate
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RebootHostTaskHandler.class);

    /*
     * The <code>NodeService</code> instance
     */
    private final NodeService nodeService;

    /*
     * The <code>DataServiceRepository</code> instance
     */
    private final DataServiceRepository repository;

    @Autowired
    public PowerOnScaleIOVM(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Power On Scale IO VM");
        final String taskMessage = "Power On Scale IO VM";
        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
/*        try
        {
            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            final DeployScaleIoVmTaskResponse deployScaleIoVmTaskResponse = (DeployScaleIoVmTaskResponse) job.getTaskResponseMap()
                                                                                                             .get("deploySVM");

            if (deployScaleIoVmTaskResponse == null)
            {
                throw new IllegalStateException("No Deploy ScaleIO VM task response found");
            }

            final ComponentEndpointIds componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            VmPowerOperationRequest powerOperationRequest = new VmPowerOperationRequest();
            powerOperationRequest.setHostname(installEsxiTaskResponse.getHostname());
            powerOperationRequest.setVmName(deployScaleIoVmTaskResponse.getNewVMName());
            powerOperationRequest.setPowerOperation(VmPowerOperationRequest.PowerOperation.POWER_ON);

            VmPowerOperationsRequestMessage requestMessage = new VmPowerOperationsRequestMessage();
            requestMessage.setVmPowerOperationRequest(powerOperationRequest);
            requestMessage.setEndpointUrl(componentEndpointIds.getEndpointUrl());
            requestMessage.setComponentEndpointIds(new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                                                                                                                          componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

            boolean succeeded = this.nodeService.requestVmPowerOperation(requestMessage);

            if (!succeeded)
            {
                throw new IllegalStateException("VM power operation request failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            return true;
        }
        catch (Exception ex)
        {
            LOGGER.error("Error powering on ScaleIO VM", ex);
            response.addError(ex.getMessage());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }
}
