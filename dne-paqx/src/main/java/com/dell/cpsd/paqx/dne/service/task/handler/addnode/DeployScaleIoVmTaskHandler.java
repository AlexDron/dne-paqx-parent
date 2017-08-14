package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.DeployScaleIoVmTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.NodeExpansionRequest;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.VirtualMachineCloneSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class DeployScaleIoVmTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER                   = LoggerFactory.getLogger(DeployScaleIoVmTaskHandler.class);
    private static final String SCALEIO_VM_PREFIX        = "ScaleIO-";
    private static final String SCALEIO_TEMPLATE_VM_NAME = "EMC ScaleIO SVM Template.*";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public DeployScaleIoVmTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Deploy ScaleIO VM From Template task");

        final DeployScaleIoVmTaskResponse response = initializeResponse(job);

        try
        {
            final Validate validate = new Validate(job).invoke();
            final ComponentEndpointIds componentEndpointIds = validate.getComponentEndpointIds();
            final String hostname = validate.getHostname();
            final String newScaleIoVmName = validate.getNewScaleIoVmName();
            final String dataCenterName = validate.getDataCenterName();

            final DeployVMFromTemplateRequestMessage requestMessage = getDeployVMFromTemplateRequestMessage(componentEndpointIds, hostname,
                    newScaleIoVmName, dataCenterName);

            final boolean success = this.nodeService.requestDeployScaleIoVm(requestMessage);

            response.setWorkFlowTaskStatus(success ? Status.SUCCEEDED : Status.FAILED);
            response.setNewVMName(newScaleIoVmName);

            return success;
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
            response.addError(e.toString());
            return false;
        }
    }

    private DeployVMFromTemplateRequestMessage getDeployVMFromTemplateRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String newScaleIoVmName, final String dataCenterName)
    {
        final DeployVMFromTemplateRequestMessage requestMessage = new DeployVMFromTemplateRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostName(hostname);
        requestMessage.setTemplateName(SCALEIO_TEMPLATE_VM_NAME);
        requestMessage.setNewVMName(newScaleIoVmName);
        requestMessage.setDatacenterName(dataCenterName);
        final VirtualMachineCloneSpec virtualMachineCloneSpec = new VirtualMachineCloneSpec();
        virtualMachineCloneSpec.setPoweredOn(true);
        virtualMachineCloneSpec.setTemplate(false);
        requestMessage.setVirtualMachineCloneSpec(virtualMachineCloneSpec);
        return requestMessage;
    }

    @Override
    public DeployScaleIoVmTaskResponse initializeResponse(Job job)
    {
        final DeployScaleIoVmTaskResponse response = new DeployScaleIoVmTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    private class Validate
    {
        private final Job                  job;
        private       ComponentEndpointIds componentEndpointIds;
        private       String               hostname;
        private       String               dataCenterName;
        private       String               newScaleIoVmName;

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

            final NodeExpansionRequest inputParams = job.getInputParams();

            if (inputParams == null)
            {
                throw new IllegalStateException("Job Input Params are null");
            }

            final String clusterName = inputParams.getClusterName();

            if (clusterName == null)
            {
                throw new IllegalStateException("Cluster Name is null");
            }

            dataCenterName = repository.getDataCenterName(clusterName);

            if (dataCenterName == null)
            {
                throw new IllegalStateException("DataCenter name is null");
            }

            final String scaleIOSVMManagementIpAddress = inputParams.getScaleIOSVMManagementIpAddress();

            if (scaleIOSVMManagementIpAddress == null)
            {
                throw new IllegalStateException("ScaleIO Management IP Address is null");
            }

            newScaleIoVmName = SCALEIO_VM_PREFIX + scaleIOSVMManagementIpAddress;
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

        String getDataCenterName()
        {
            return dataCenterName;
        }

        String getNewScaleIoVmName()
        {
            return newScaleIoVmName;
        }
    }
}
