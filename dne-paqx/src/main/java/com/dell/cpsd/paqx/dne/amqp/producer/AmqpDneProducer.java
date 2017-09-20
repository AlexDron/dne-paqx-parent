/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.ChangeIdracCredentialsRequestMessage;
import com.dell.cpsd.CompleteNodeAllocationRequestMessage;
import com.dell.cpsd.ConfigureBootDeviceIdracRequestMessage;
import com.dell.cpsd.ConfigurePxeBootRequestMessage;
import com.dell.cpsd.InstallESXiRequestMessage;
import com.dell.cpsd.ListNodes;
import com.dell.cpsd.NodeInventoryRequestMessage;
import com.dell.cpsd.SetObmSettingsRequestMessage;
import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityData;
import com.dell.cpsd.hdp.capability.registry.client.helper.AmqpProviderEndpointHelper;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateProtectionDomainsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.*;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishIdracNetwokSettings(IdracNetworkSettingsRequestMessage request)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishIdracNetwokSettings");
            return;
        }

        LOGGER.info("publishIdracNetwokSettings: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigureBootDeviceIdrac(ConfigureBootDeviceIdracRequestMessage request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishConfigureBootDeviceIdrac");
            return;
        }

        LOGGER.info("publishConfigureBootDeviceIdrac: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigurePxeBoot(ConfigurePxeBootRequestMessage request) {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishConfigurePxeboot");
            return;
        }

        LOGGER.info("publishConfigurePxeBoot: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ConfigurePxeBootRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish configure Pxe Boot request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishConfigureObmSettings(SetObmSettingsRequestMessage request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishConfigureObmSettings");
            return;
        }

        LOGGER.info("publishConfigureObmSettings: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);

            if (messageType(SetObmSettingsRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish configure obm settings request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
            if (messageType(InstallESXiRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Install ESXi License request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(ClusterOperationRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Add Host to VCenter request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(SoftwareVIBRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Install ScaleIo VIB request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(SoftwareVIBConfigureRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Configure ScaleIo VIB request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(AddHostToDvSwitchRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Add Host to DV Switch request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(DeployVMFromTemplateRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish deploy VM from template request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(EnablePCIPassthroughRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish enable PCI pass through request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(HostPowerOperationRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish reboot host request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(UpdatePCIPassthruSVMRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish Set PCI Pass through request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
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
            if (messageType(AddEsxiHostVSphereLicenseRequest.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish apply ESXi license request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListExsiCredentialDetails(final ListEsxiCredentialDetailsRequestMessage requestMessage)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishListExsiCredentialDetails");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(ListEsxiCredentialDetailsRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish list esxi credential details request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishHostMaintenanceMode(final HostMaintenanceModeRequestMessage requestMessage)
    {
        final Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishHostMaintenanceMode");
            return;
        }

        capabilities.stream().filter(Objects::nonNull).forEach(capabilityData -> {
            final ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            final AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(HostMaintenanceModeRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Publish ESXi host maintenance mode request from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishListNodes(ListNodes request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishListNodes");
            return;
        }

        LOGGER.info("publishListNodes: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDiscoverClusters(DiscoverClusterRequestInfoMessage request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishDiscoverClusters");
            return;
        }

        LOGGER.info("publishDiscoverClusters: found list of capablities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishValidateClusters(ValidateVcenterClusterRequestMessage request)
    {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishValidateStorage(EssValidateStoragePoolRequestMessage requestMessage)
    {
        // At this phase ESS is for DNE internal use only so no capability registry for ESS, use exchange, routing key directly.
        LOGGER.info("Send request to ESS validation for storage pools.");
        rabbitTemplate.convertAndSend(essRequestExchange, essReqRoutingKeyPrefix, requestMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishCompleteNodeAllocation(CompleteNodeAllocationRequestMessage request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishCompleteNodeAllocation");
            return;
        }

        CapabilityData capabilityData = capabilities.stream()
                .filter((data) -> "manage-node-allocation".equals(data.getCapability().getProfile())).findFirst().orElse(null);

        if (capabilityData != null)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);

            LOGGER.info("Send complete node allocation request message from DNE paqx.");
            rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishNodeInventoryDiscovery(final NodeInventoryRequestMessage request)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishNodeInventoryDiscovery");
            return;
        }

        LOGGER.info("publishNodeInventoryDiscovery: found list of capabilities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(NodeInventoryRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send node inventory discovery request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), request);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishDatastoreRename(DatastoreRenameRequestMessage requestMessage)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishDatastoreRename");
            return;
        }

        LOGGER.info("publishDatastoreRename: found list of capabilities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(DatastoreRenameRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send datastore rename request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishUpdateSoftwareAcceptance(final VCenterUpdateSoftwareAcceptanceRequestMessage requestMessage)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishUpdateSoftwareAcceptance");
            return;
        }

        LOGGER.info("publishUpdateSoftwareAcceptance: found list of capabilities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(VCenterUpdateSoftwareAcceptanceRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send update software acceptance message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
                break;
            }
        }
    }

    @Override
    public void publishValidateProtectionDomain(EssValidateProtectionDomainsRequestMessage requestMessage)
    {
        Collection<CapabilityData> capabilities = capabilityBinder.getCurrentCapabilities();

        if (capabilities == null)
        {
            LOGGER.error("No Capabilities found for publishValidateProtectionDomain");
            return;
        }

        LOGGER.info("publishValidateProtectionDomain: found list of capabilities with size {}", capabilities.size());

        for (CapabilityData capabilityData : capabilities)
        {
            ProviderEndpoint endpoint = capabilityData.getCapability().getProviderEndpoint();
            AmqpProviderEndpointHelper endpointHelper = new AmqpProviderEndpointHelper(endpoint);
            if (messageType(DatastoreRenameRequestMessage.class).equals(endpointHelper.getRequestMessageType()))
            {
                LOGGER.info("Send validate protection domain request message from DNE paqx.");
                rabbitTemplate.convertAndSend(endpointHelper.getRequestExchange(), endpointHelper.getRequestRoutingKey(), requestMessage);
                break;
            }
        }
    }

    private String messageType(Class messageClass)
    {
        Message messageAnnotation = (Message) messageClass.getAnnotation(Message.class);
        return messageAnnotation.value();
    }
}
