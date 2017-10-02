/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.delegates;

import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.delegates.model.NodeDetail;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DeployScaleIoVmTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.EnablePciPassThroughTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
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
@Qualifier("updatePCIPassThrough")
public class UpdatePCIPassThrough extends BaseWorkflowDelegate
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePCIPassThrough.class);

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    @Autowired
    public UpdatePCIPassThrough(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    private UpdatePCIPassthruSVMRequestMessage getUpdatePCIPassthruSVMRequestMessage(final String hostname, final String hostPciDeviceId,
            final String newVMName, final ComponentEndpointIds componentEndpointIds)
    {
        final UpdatePCIPassthruSVMRequestMessage requestMessage = new UpdatePCIPassthruSVMRequestMessage();
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPciDeviceId);
        requestMessage.setVmName(newVMName);
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        return requestMessage;
    }

    @Override
    public void delegateExecute(final DelegateExecution delegateExecution)
    {
        LOGGER.info("Execute Update/Set PCI pass through task");
        final String taskMessage = "Set PCI Pass Through";

        final NodeDetail nodeDetail = (NodeDetail) delegateExecution.getVariable(NODE_DETAIL);
        /*try
        {
            final Validate validate = new Validate(job).invoke();
            final String hostname = validate.getHostname();
            final String hostPciDeviceId = validate.getHostPciDeviceId();
            final String newVMName = validate.getNewVMName();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();

            final UpdatePCIPassthruSVMRequestMessage requestMessage = getUpdatePCIPassthruSVMRequestMessage(hostname, hostPciDeviceId,
                                                                                                            newVMName, componentEndpointIds);

            final boolean success = this.nodeService.requestSetPciPassThrough(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Configure PCI PassThrough Failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);

            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.getMessage());
        }

        response.setWorkFlowTaskStatus(Status.FAILED);
        return false;*/

        LOGGER.info(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
        updateDelegateStatus(taskMessage + " on Node " + nodeDetail.getServiceTag() + " was successful.");
    }

    private class Validate
    {
        private final Job                  job;
        private       ComponentEndpointIds componentEndpointIds;
        private       String               hostname;
        private       String               hostPciDeviceId;
        private       String               newVMName;

        Validate(final Job job)
        {
            this.job = job;
        }

        Validate invoke()
        {
            componentEndpointIds = repository.getVCenterComponentEndpointIdsByEndpointType("VCENTER-CUSTOMER");

            if (componentEndpointIds == null)
            {
                throw new IllegalStateException("No VCenter components found.");
            }

            final InstallEsxiTaskResponse installEsxiTaskResponse = (InstallEsxiTaskResponse) job.getTaskResponseMap().get("installEsxi");

            if (installEsxiTaskResponse == null)
            {
                throw new IllegalStateException("No Install ESXi task response found");
            }

            hostname = installEsxiTaskResponse.getHostname();

            if (hostname == null)
            {
                throw new IllegalStateException("Host name is null");
            }

            final EnablePciPassThroughTaskResponse enablePciPassThroughTaskResponse = (EnablePciPassThroughTaskResponse) job
                    .getTaskResponseMap().get("enablePciPassthroughHost");

            if (enablePciPassThroughTaskResponse == null)
            {
                throw new IllegalStateException("Enable PCI Task Response is null");
            }

            hostPciDeviceId = enablePciPassThroughTaskResponse.getHostPciDeviceId();

            if (hostPciDeviceId == null)
            {
                throw new IllegalStateException("Host PCI Device ID is null");
            }

            final DeployScaleIoVmTaskResponse deployScaleIoVmTaskResponse = (DeployScaleIoVmTaskResponse) job.getTaskResponseMap()
                    .get("deploySVM");

            if (deployScaleIoVmTaskResponse == null)
            {
                throw new IllegalStateException("Deploy ScaleIO VM Task Response is null");
            }

            newVMName = deployScaleIoVmTaskResponse.getNewVMName();

            if (newVMName == null)
            {
                throw new IllegalStateException("New Virtual Machine name is null");
            }

            return this;
        }

        ComponentEndpointIds getComponentEndpointIds()
        {
            return componentEndpointIds;
        }

        String getHostname()
        {
            return hostname;
        }

        String getHostPciDeviceId()
        {
            return hostPciDeviceId;
        }

        String getNewVMName()
        {
            return newVMName;
        }
    }
}
