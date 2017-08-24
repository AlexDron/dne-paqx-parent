/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.task.handler.addnode;

import com.dell.cpsd.paqx.dne.domain.IWorkflowTaskHandler;
import com.dell.cpsd.paqx.dne.domain.Job;
import com.dell.cpsd.paqx.dne.domain.vcenter.PciDevice;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.EnablePciPassThroughTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.InstallEsxiTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.Status;
import com.dell.cpsd.paqx.dne.service.task.handler.BaseTaskHandler;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Enable PCI PassThrough Task Handler
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class EnablePciPassthroughTaskHandler extends BaseTaskHandler implements IWorkflowTaskHandler
{
    /**
     * The logger instance
     */
    private static final Logger LOGGER            = LoggerFactory.getLogger(EnablePciPassthroughTaskHandler.class);
    private static final String DELL_PCI_REGEX    = "Dell.*(H730|HBA330).*Mini*";
    private static final String PCI_BUS_DEVICE_ID = "0000:02:00.0";

    /**
     * The <code>NodeService</code> instance
     */
    private final NodeService           nodeService;
    private final DataServiceRepository repository;

    public EnablePciPassthroughTaskHandler(final NodeService nodeService, final DataServiceRepository repository)
    {
        this.nodeService = nodeService;
        this.repository = repository;
    }

    @Override
    public boolean executeTask(final Job job)
    {
        LOGGER.info("Execute Enable PCI Pass through task");

        final EnablePciPassThroughTaskResponse response = initializeResponse(job);

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
                throw new IllegalStateException("Hostname is null");
            }

            final List<PciDevice> pciDeviceList = repository.getPciDeviceList();

            if (pciDeviceList == null || pciDeviceList.isEmpty())
            {
                throw new IllegalStateException("PCI Device List is empty");
            }

            final String hostPciDeviceId = filterDellPercPciDeviceId(pciDeviceList);

            final EnablePCIPassthroughRequestMessage requestMessage = getEnablePCIPassthroughRequestMessage(componentEndpointIds, hostname,
                    hostPciDeviceId);

            final boolean success = this.nodeService.requestEnablePciPassThrough(requestMessage);

            if (!success)
            {
                throw new IllegalStateException("Enable PCI PassThrough Failed");
            }

            response.setWorkFlowTaskStatus(Status.SUCCEEDED);
            response.setHostPciDeviceId(hostPciDeviceId);

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

    private EnablePCIPassthroughRequestMessage getEnablePCIPassthroughRequestMessage(final ComponentEndpointIds componentEndpointIds,
            final String hostname, final String hostPciDeviceId)
    {
        final EnablePCIPassthroughRequestMessage requestMessage = new EnablePCIPassthroughRequestMessage();
        requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
        requestMessage.setComponentEndpointIds(
                new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                        componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));
        requestMessage.setHostname(hostname);
        requestMessage.setHostPciDeviceId(hostPciDeviceId);
        return requestMessage;
    }

    @Override
    public EnablePciPassThroughTaskResponse initializeResponse(Job job)
    {
        final EnablePciPassThroughTaskResponse response = new EnablePciPassThroughTaskResponse();
        response.setWorkFlowTaskName(job.getCurrentTask().getTaskName());
        response.setWorkFlowTaskStatus(Status.IN_PROGRESS);
        job.addTaskResponse(job.getStep(), response);

        return response;
    }

    private String filterDellPercPciDeviceId(final List<PciDevice> pciDeviceList) throws IllegalStateException
    {
        final PciDevice requiredPciDevice = pciDeviceList.stream()
                .filter(obj -> Objects.nonNull(obj) && obj.getDeviceName().matches(DELL_PCI_REGEX))
                .findFirst().orElse(null);

        if (requiredPciDevice == null)
        {
            throw new IllegalStateException("Unable to find matching PCI device");
        }

        return requiredPciDevice.getDeviceId();
    }

}
