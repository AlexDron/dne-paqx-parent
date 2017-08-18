/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.*;
import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityData;
import com.dell.cpsd.hdp.capability.registry.client.helper.AmqpProviderEndpointHelper;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Objects;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */

public class AmqpDneProducer implements DneProducer
{
    @Autowired
    private String essRequestExchange;

    @Autowired
    private String essReqRoutingKeyPrefix;

    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpDneProducer.class);
    private final RabbitTemplate   rabbitTemplate;
    private final CapabilityBinder capabilityBinder;

    public AmqpDneProducer(RabbitTemplate rabbitTemplate, CapabilityBinder capabilityBinder)
    {
        this.rabbitTemplate = rabbitTemplate;
        this.capabilityBinder = capabilityBinder;
    }

    @Override
    public void publishIdracNetwokSettings(IdracNetworkSettingsRequestMessage request)
    {
        Collection<CapabilityData> capabilityDatas = capabilityBinder.getCurrentCapabilities();
        LOGGER.info("publishIdracNetwokSettings: found list of capablities with size {}", capabilityDatas.size());
        for (CapabilityData capabilityData : capabilityDatas)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(IdracNetworkSettingsRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish idrac network settings request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    @Override
    public void publishConfigureBootDeviceIdrac(ConfigureBootDeviceIdracRequestMessage request)
    {
        Collection<CapabilityData> capabilityDatas = capabilityBinder.getCurrentCapabilities();
        LOGGER.info("publishConfigureBootDeviceIdrac: found list of capablities with size {}", capabilityDatas.size());
        for (CapabilityData capabilityData : capabilityDatas)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ConfigureBootDeviceIdracRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish configure boot device idrac request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    @Override
    public void publishListScaleIoComponents(final ListComponentRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishListScaleIoComponents");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ListComponentRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish List ScaleIO Components request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishListVCenterComponents(final ListComponentsRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishListVCenterComponents");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ListComponentsRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish List VCenter Components request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishDiscoverScaleIo(final ListStorageRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishDiscoverScaleIo");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ListStorageRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Discover ScaleIO request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishDiscoverVcenter(final DiscoveryRequestInfoMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishDiscoverVcenter");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(DiscoveryRequestInfoMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Discover VCenter request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishInstallEsxiRequest(final InstallESXiRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishInstallEsxiRequest");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(InstallESXiResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Install ESXi License request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishAddHostToVCenter(final ClusterOperationRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishAddHostToVCenter");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ClusterOperationResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Add Host to VCenter request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishInstallScaleIoVib(final SoftwareVIBRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishInstallScaleIoVib");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(SoftwareVIBResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Install ScaleIo VIB request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishConfigureScaleIoVib(final SoftwareVIBConfigureRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishConfigureScaleIoVib");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(SoftwareVIBResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Configure ScaleIo VIB request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishAddHostToDvSwitch(final AddHostToDvSwitchRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishAddHostToDvSwitch");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(AddHostToDvSwitchResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Add Host to DV Switch request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishDeployVmFromTemplate(final DeployVMFromTemplateRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishDeployVmFromTemplate");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(DeployVMFromTemplateResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish deploy VM from template request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishEnablePciPassthrough(final EnablePCIPassthroughRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishEnablePciPassthrough");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(EnablePCIPassthroughResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish enable PCI pass through request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishRebootHost(final HostPowerOperationRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishRebootHost");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(HostPowerOperationResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish reboot host request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishSetPciPassthrough(final UpdatePCIPassthruSVMRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishSetPciPassthrough");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(UpdatePCIPassthruSVMResponseMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Set PCI Pass through request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishApplyEsxiLicense(final AddEsxiHostVSphereLicenseRequest request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishApplyEsxiLicense");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(AddEsxiHostVSphereLicenseResponse.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish apply ESXi license request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    @Override
    public void publishListNodes(ListNodes request)
    {
        Collection<CapabilityData> capabilityDatas = capabilityBinder.getCurrentCapabilities();
        LOGGER.info("publishListNodes: found list of capablities with size {}", capabilityDatas.size());
        for (CapabilityData capabilityData : capabilityDatas)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ListNodes.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send node discovery request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    @Override
    public void publishDiscoverClusters(DiscoverClusterRequestInfoMessage request)
    {
        Collection<CapabilityData> capabilityDatas = capabilityBinder.getCurrentCapabilities();
        LOGGER.info("publishDiscoverClusters: found list of capablities with size {}", capabilityDatas.size());
        for (CapabilityData capabilityData : capabilityDatas)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(DiscoverClusterRequestInfoMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send discover cluster request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    @Override
    public void publishValidateClusters(ValidateVcenterClusterRequestMessage request) {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, request);
    }

    /**
     * Send the <code>CompleteNodeAllocationRequestMessage</code> to the node 
     * discovery service.
     * 
     * @param request - The <code>CompleteNodeAllocationRequestMessage</code> instance
     */
    @Override
    public void publishCompleteNodeAllocation(CompleteNodeAllocationRequestMessage request)
    {
        CapabilityData capabilityData = capabilityBinder.getCurrentCapabilities()
                .stream()
                .filter((data) -> "manage-node-allocation".equals(data.getCapability().getProfile()))
                .findFirst()
                .orElse(null);
        
        if (capabilityData != null) 
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
                    
            LOGGER.info("Send complete node allocation request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    private String messageType(Class messageClass)
    {
        Message messageAnnotation = (Message)messageClass.getAnnotation(Message.class);
        return messageAnnotation.value();
    }

    @Override
    /**
     * Send the <code>ChangeIdracCredentialsRequestMessage</code> to the node 
     * discovery service.
     * 
     * @param request - The <code>ChangeIdracCredentialsRequestMessage</code> instance
     */
    public void publishChangeIdracCredentials(ChangeIdracCredentialsRequestMessage request)
    {
        Collection<CapabilityData> capabilityDatas = capabilityBinder.getCurrentCapabilities();
        LOGGER.info("publishChangeIdracCrdentials: found list of capablities with size {}", capabilityDatas.size());
        for (CapabilityData capabilityData : capabilityDatas)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ChangeIdracCredentialsRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish change idrac credentials request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }
}
