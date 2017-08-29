/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.amqp.producer;

import com.dell.cpsd.ChangeIdracCredentialsRequestMessage;
import com.dell.cpsd.CompleteNodeAllocationRequestMessage;
import com.dell.cpsd.ConfigureBootDeviceIdracRequestMessage;
import com.dell.cpsd.InstallESXiRequestMessage;
import com.dell.cpsd.ListNodes;
import com.dell.cpsd.common.rabbitmq.annotation.Message;
import com.dell.cpsd.hdp.capability.registry.api.Capability;
import com.dell.cpsd.hdp.capability.registry.api.EndpointProperty;
import com.dell.cpsd.hdp.capability.registry.api.ProviderEndpoint;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityBinder;
import com.dell.cpsd.hdp.capability.registry.client.binder.CapabilityData;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.service.engineering.standards.EssValidateStoragePoolRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ValidateVcenterClusterRequestMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * The tests for the AmqpDneProducer class.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AmqpDneProducerTest
{
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private CapabilityBinder capabilityBinder;

    @Mock
    private CapabilityData capabilityData;

    @Mock
    private Capability capability;

    @Mock
    private ProviderEndpoint providerEndpoint;

    private AmqpDneProducer producer;
    private List<CapabilityData> capabilityDataList;
    private List<EndpointProperty> endpointProperties;
    private String exchange = "the_exchange";
    private String routingKey = "the_routing_key";

    @Before
    public void setUp() throws Exception
    {
        this.producer = new AmqpDneProducer(this.rabbitTemplate, this.capabilityBinder);

        this.capabilityDataList = new ArrayList<>();
        this.endpointProperties = new ArrayList<>();

        this.capabilityDataList.add(this.capabilityData);

        EndpointProperty property = new EndpointProperty();
        property.setName("request-exchange");
        property.setValue(this.exchange);
        this.endpointProperties.add(property);

        property = new EndpointProperty();
        property.setName("request-routing-key");
        property.setValue(this.routingKey);
        this.endpointProperties.add(property);
    }

    @Test
    public void publishIdracNetwokSettings() throws Exception
    {
        this.executeTest(mock(IdracNetworkSettingsRequestMessage.class), this.producer::publishIdracNetwokSettings);
    }

    @Test
    public void publishIdracNetwokSettings_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(IdracNetworkSettingsRequestMessage.class), this.producer::publishIdracNetwokSettings);
    }

    @Test
    public void publishConfigureBootDeviceIdrac() throws Exception
    {
        this.executeTest(mock(ConfigureBootDeviceIdracRequestMessage.class), this.producer::publishConfigureBootDeviceIdrac);
    }

    @Test
    public void publishConfigureBootDeviceIdrac_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ConfigureBootDeviceIdracRequestMessage.class), this.producer::publishConfigureBootDeviceIdrac);
    }

    @Test
    public void publishListScaleIoComponents() throws Exception
    {
        this.executeTest(mock(ListComponentRequestMessage.class), this.producer::publishListScaleIoComponents);
    }

    @Test
    public void publishListScaleIoComponents_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ListComponentRequestMessage.class), this.producer::publishListScaleIoComponents);
    }

    @Test
    public void publishListVCenterComponents() throws Exception
    {
        this.executeTest(mock(ListComponentsRequestMessage.class), this.producer::publishListVCenterComponents);
    }

    @Test
    public void publishListVCenterComponents_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ListComponentsRequestMessage.class), this.producer::publishListVCenterComponents);
    }

    @Test
    public void publishDiscoverScaleIo() throws Exception
    {
        this.executeTest(mock(ListStorageRequestMessage.class), this.producer::publishDiscoverScaleIo);
    }

    @Test
    public void publishDiscoverScaleIo_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ListStorageRequestMessage.class), this.producer::publishDiscoverScaleIo);
    }

    @Test
    public void publishDiscoverVcenter() throws Exception
    {
        this.executeTest(mock(DiscoveryRequestInfoMessage.class), this.producer::publishDiscoverVcenter);
    }

    @Test
    public void publishDiscoverVcenter_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(DiscoveryRequestInfoMessage.class), this.producer::publishDiscoverVcenter);
    }

    @Test
    public void publishInstallEsxiRequest() throws Exception
    {
        this.executeTest(mock(InstallESXiRequestMessage.class), this.producer::publishInstallEsxiRequest);
    }

    @Test
    public void publishInstallEsxiRequest_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(InstallESXiRequestMessage.class), this.producer::publishInstallEsxiRequest);
    }

    @Test
    public void publishAddHostToVCenter() throws Exception
    {
        this.executeTest(mock(ClusterOperationRequestMessage.class), this.producer::publishAddHostToVCenter);
    }

    @Test
    public void publishAddHostToVCenter_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ClusterOperationRequestMessage.class), this.producer::publishAddHostToVCenter);
    }

    @Test
    public void publishInstallScaleIoVib() throws Exception
    {
        this.executeTest(mock(SoftwareVIBRequestMessage.class), this.producer::publishInstallScaleIoVib);
    }

    @Test
    public void publishInstallScaleIoVib_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(SoftwareVIBRequestMessage.class), this.producer::publishInstallScaleIoVib);
    }

    @Test
    public void publishConfigureScaleIoVib() throws Exception
    {
        this.executeTest(mock(SoftwareVIBConfigureRequestMessage.class), this.producer::publishConfigureScaleIoVib);
    }

    @Test
    public void publishConfigureScaleIoVib_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(SoftwareVIBConfigureRequestMessage.class), this.producer::publishConfigureScaleIoVib);
    }

    @Test
    public void publishAddHostToDvSwitch() throws Exception
    {
        this.executeTest(mock(AddHostToDvSwitchRequestMessage.class), this.producer::publishAddHostToDvSwitch);
    }

    @Test
    public void publishAddHostToDvSwitch_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(AddHostToDvSwitchRequestMessage.class), this.producer::publishAddHostToDvSwitch);
    }

    @Test
    public void publishDeployVmFromTemplate() throws Exception
    {
        this.executeTest(mock(DeployVMFromTemplateRequestMessage.class), this.producer::publishDeployVmFromTemplate);
    }

    @Test
    public void publishDeployVmFromTemplate_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(DeployVMFromTemplateRequestMessage.class), this.producer::publishDeployVmFromTemplate);
    }

    @Test
    public void publishEnablePciPassthrough() throws Exception
    {
        this.executeTest(mock(EnablePCIPassthroughRequestMessage.class), this.producer::publishEnablePciPassthrough);
    }

    @Test
    public void publishEnablePciPassthrough_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(EnablePCIPassthroughRequestMessage.class), this.producer::publishEnablePciPassthrough);
    }

    @Test
    public void publishRebootHost() throws Exception
    {
        this.executeTest(mock(HostPowerOperationRequestMessage.class), this.producer::publishRebootHost);
    }

    @Test
    public void publishRebootHost_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(HostPowerOperationRequestMessage.class), this.producer::publishRebootHost);
    }

    @Test
    public void publishSetPciPassthrough() throws Exception
    {
        this.executeTest(mock(UpdatePCIPassthruSVMRequestMessage.class), this.producer::publishSetPciPassthrough);
    }

    @Test
    public void publishSetPciPassthrough_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(UpdatePCIPassthruSVMRequestMessage.class), this.producer::publishSetPciPassthrough);
    }

    @Test
    public void publishApplyEsxiLicense() throws Exception
    {
        this.executeTest(mock(AddEsxiHostVSphereLicenseRequest.class), this.producer::publishApplyEsxiLicense);
    }

    @Test
    public void publishApplyEsxiLicense_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(AddEsxiHostVSphereLicenseRequest.class), this.producer::publishApplyEsxiLicense);
    }

    @Test
    public void publishListExsiCredentialDetails() throws Exception
    {
        this.executeTest(mock(ListEsxiCredentialDetailsRequestMessage.class), this.producer::publishListExsiCredentialDetails);
    }

    @Test
    public void publishListExsiCredentialDetails_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ListEsxiCredentialDetailsRequestMessage.class), this.producer::publishListExsiCredentialDetails);
    }

    @Test
    public void publishEsxiHostExitMaintenanceMode() throws Exception
    {
        this.executeTest(mock(HostMaintenanceModeRequestMessage.class), this.producer::publishEsxiHostExitMaintenanceMode);
    }

    @Test
    public void publishEsxiHostExitMaintenanceMode_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(HostMaintenanceModeRequestMessage.class), this.producer::publishEsxiHostExitMaintenanceMode);
    }

    @Test
    public void publishListNodes() throws Exception
    {
        this.executeTest(mock(ListNodes.class), this.producer::publishListNodes);
    }

    @Test
    public void publishListNodes_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(ListNodes.class), this.producer::publishListNodes);
    }

    @Test
    public void publishDiscoverClusters() throws Exception
    {
        this.executeTest(mock(DiscoverClusterRequestInfoMessage.class), this.producer::publishDiscoverClusters);
    }

    @Test
    public void publishDiscoverClusters_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(DiscoverClusterRequestInfoMessage.class), this.producer::publishDiscoverClusters);
    }

    @Test
    public void publishValidateClusters() throws Exception
    {
        ValidateVcenterClusterRequestMessage request = mock(ValidateVcenterClusterRequestMessage.class);

        ReflectionTestUtils.setField(this.producer, "essRequestExchange", this.exchange);
        ReflectionTestUtils.setField(this.producer, "essReqRoutingKeyPrefix", this.routingKey);

        this.producer.publishValidateClusters(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishValidateStorage() throws Exception
    {
        EssValidateStoragePoolRequestMessage request = mock(EssValidateStoragePoolRequestMessage.class);

        ReflectionTestUtils.setField(this.producer, "essRequestExchange", this.exchange);
        ReflectionTestUtils.setField(this.producer, "essReqRoutingKeyPrefix", this.routingKey);

        this.producer.publishValidateStorage(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishCompleteNodeAllocation() throws Exception
    {
        CompleteNodeAllocationRequestMessage request = mock(CompleteNodeAllocationRequestMessage.class);
        String profile = "manage-node-allocation";

        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue(((Message)request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(profile).when(this.capability).getProfile();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        this.producer.publishCompleteNodeAllocation(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    @Test
    public void publishCompleteNodeAllocation_no_capabilities() throws Exception
    {
        this.executeTest_no_capabilities(mock(CompleteNodeAllocationRequestMessage.class), this.producer::publishCompleteNodeAllocation);
    }

    @Test
    public void publishChangeIdracCredentials() throws Exception
    {
        this.executeTest(mock(ChangeIdracCredentialsRequestMessage.class), this.producer::publishChangeIdracCredentials);
    }

    private <T> void executeTest(T request, Consumer<T> consumer)
    {
        EndpointProperty property = new EndpointProperty();
        property.setName("request-message-type");
        property.setValue(((Message)request.getClass().getAnnotation(Message.class)).value());
        this.endpointProperties.add(property);

        doReturn(this.capabilityDataList).when(this.capabilityBinder).getCurrentCapabilities();
        doReturn(this.capability).when(this.capabilityData).getCapability();
        doReturn(this.providerEndpoint).when(this.capability).getProviderEndpoint();
        doReturn(this.endpointProperties).when(this.providerEndpoint).getEndpointProperties();

        consumer.accept(request);

        verify(this.rabbitTemplate).convertAndSend(this.exchange, this.routingKey, request);
    }

    private <T> void executeTest_no_capabilities(T request, Consumer<T> consumer)
    {
        List<Capability> nullCapabilities = null;

        doReturn(nullCapabilities).when(this.capabilityBinder).getCurrentCapabilities();

        consumer.accept(request);

        verify(this.rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(request.getClass()));
    }
}