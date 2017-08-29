/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.dell.cpsd.EsxiInstallationInfo;
import com.dell.cpsd.InstallESXiResponseMessage;
import com.dell.cpsd.paqx.dne.domain.scaleio.ScaleIOData;
import com.dell.cpsd.paqx.dne.domain.vcenter.VCenter;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.transformers.DiscoveryInfoToVCenterDomainTransformer;
import com.dell.cpsd.paqx.dne.transformers.ScaleIORestToScaleIODomainTransformer;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponse;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ScaleIOComponentDetails;
import com.dell.cpsd.storage.capabilities.api.ScaleIOSystemDataRestRep;
import com.dell.cpsd.storage.capabilities.api.ScaleIoEndpointDetails;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseRequest;
import com.dell.cpsd.virtualization.capabilities.api.AddEsxiHostVSphereLicenseResponse;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ClusterOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.Datacenter;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.DeployVMFromTemplateResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListEsxiCredentialDetailsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBConfigureRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.SoftwareVIBResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterComponentDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterCredentialDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterEndpointDetails;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.dell.cpsd.ChangeIdracCredentialsResponseMessage;
import com.dell.cpsd.CompleteNodeAllocationResponseMessage;
import com.dell.cpsd.ConfigureBootDeviceIdracError;
import com.dell.cpsd.ConfigureBootDeviceIdracResponseMessage;
import com.dell.cpsd.NodeAllocationInfo;
import com.dell.cpsd.NodeAllocationInfo.AllocationStatus;
import com.dell.cpsd.NodesListed;
import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import com.dell.cpsd.paqx.dne.service.model.ConfigureBootDeviceIdracRequest;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;

import com.dell.cpsd.service.common.client.callback.ServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceError;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.common.client.rpc.DefaultMessageConsumer;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfo;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.MessageProperties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The tests for AmqpNodeService class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AmqpNodeServiceTest
{
    /**
     * Test that the listDiscoveredNodes method can hadle a timeout.
     * 
     * @throws Exception
     */
    @Test(expected = ServiceTimeoutException.class)
    public void testListDiscoveredNodesTimeout() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                throw new ServiceTimeoutException("blah");
            }
        };

        nodeService.listDiscoveredNodes();
    }

    /**
     * Test that the listDiscoveredNodes method can execute successfully.
     * 
     * @throws Exception
     */
    @Test
    public void testListDiscoveredNodesSuccess() throws Exception
    {
        String convergedUuid = UUID.randomUUID().toString();
        String nodeId = UUID.randomUUID().toString();

        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                // default status of discovered
                com.dell.cpsd.DiscoveredNode node =
                        new com.dell.cpsd.DiscoveredNode(convergedUuid,
                                com.dell.cpsd.DiscoveredNode.AllocationStatus.DISCOVERED);

                com.dell.cpsd.MessageProperties messageProperties =
                        new com.dell.cpsd.MessageProperties(Calendar.getInstance().getTime(),
                                UUID.randomUUID().toString(), "replyToMe");

                NodesListed listed = new NodesListed(messageProperties, Arrays.asList(node));
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, listed, null));
            }
        };

        List<DiscoveredNode> discovereds = nodeService.listDiscoveredNodes();
        Assert.assertEquals(1, discovereds.size());

        DiscoveredNode discovered = discovereds.get(0);
        Assert.assertEquals(convergedUuid, discovered.getConvergedUuid());

        Mockito.verify(dneProducer, Mockito.times(1)).publishListNodes(any());
    }

    /**
     * Test that the listDiscoveredNodes method can handle any errors.
     * 
     * @throws Exception
     */
    @Test(expected = ServiceExecutionException.class)
    public void testListDiscoveredNodesError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "blah", "blah"));
            }
        };

        nodeService.listDiscoveredNodes();
    }

    /**
     * Test that the the listClusters method can handle a timeout.
     * 
     * @throws Exception
     */
    @Test(expected = ServiceTimeoutException.class)
    public void testListClustersTimeout() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe",
                Mockito.mock(DataServiceRepository.class), null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                throw new ServiceTimeoutException("blah");
            }
        };

        nodeService.listClusters();
    }

    /**
     * Test that the listClusters method can execute successfully.
     * 
     * @throws Exception
     */
    @Test
    public void testListClustersSuccess() throws Exception
    {
        String clusterName = UUID.randomUUID().toString();
        Integer numberOfHosts = 999;

        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);



        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe",
                Mockito.mock(DataServiceRepository.class), null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                DiscoverClusterResponseInfo responseInfo = new DiscoverClusterResponseInfo(Arrays.asList(
                        new ClusterInfo(clusterName, numberOfHosts)));

                MessageProperties messageProperties =
                        new MessageProperties(Calendar.getInstance().getTime(),
                                UUID.randomUUID().toString(), "replyToMe");

                DiscoverClusterResponseInfoMessage responseInfoMessage =
                        new DiscoverClusterResponseInfoMessage(messageProperties, responseInfo, DiscoverClusterResponseInfoMessage.Status.SUCCESS);
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseInfoMessage, null));
            }
        };



        List<ClusterInfo> discovereds = nodeService.listClusters();
        Assert.assertEquals(1, discovereds.size());

        ClusterInfo discovered = discovereds.get(0);
        Assert.assertEquals(clusterName, discovered.getName());
        Assert.assertEquals(numberOfHosts, discovered.getNumberOfHosts());

        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverClusters(any());
    }

    /**
     * Test that the listClusters method can handle any errors.
     * 
     * @throws Exception
     */
    @Test(expected = ServiceExecutionException.class)
    public void testListClustersError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "blah", "blah"));
            }
        };

        nodeService.listDiscoveredNodes();// ???
    }
    
    /**
     * Test that the idracNetworkSettings method can execute successfully.
     * 
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Test
    public void testIdracNetworkSettingsSuccess() throws ServiceTimeoutException, ServiceExecutionException
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        IdracNetworkSettingsRequest idracNetworkSettingsRequest = new IdracNetworkSettingsRequest("nodeId", "idracIpAddress",
                "idracGatewayIpAddress", "idracSubnetMask");

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                IdracNetworkSettingsResponse idracNetworkSettingsResponse = new IdracNetworkSettingsResponse("SUCCESS", "nodeId",
                        "idracIpAddress", "netmask", "gateway");
                com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties messageProperties = new com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties();
                messageProperties.setCorrelationId(UUID.randomUUID().toString());

                IdracNetworkSettingsResponseMessage responseMessage = new IdracNetworkSettingsResponseMessage(messageProperties,
                        idracNetworkSettingsResponse);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        IdracInfo idracInfo = nodeService.idracNetworkSettings(idracNetworkSettingsRequest);

        Assert.assertNotNull(idracInfo);
        Assert.assertEquals("SUCCESS", idracInfo.getMessage());
        Assert.assertEquals("nodeId", idracInfo.getNodeId());

        Mockito.verify(dneProducer, Mockito.times(1)).publishIdracNetwokSettings(any());
    }
    
    /**
     * Test that the idracNetworkSettings method can handle any errors.
     * 
     * @throws ServiceTimeoutException
     */
    @Test
    public void testIdracNetworkSettingsError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        IdracNetworkSettingsRequest idracNetworkSettingsRequest = new IdracNetworkSettingsRequest("nodeId", "idracIpAddress", "idracGatewayIpAddress", "idracSubnetMask");
        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
       	nodeService.idracNetworkSettings(idracNetworkSettingsRequest);
       	Mockito.verify(dneProducer, Mockito.times(1)).publishIdracNetwokSettings(any());
    }
    
    /**
     * Test that the notifyNodeAllocationComplete method executes successfully.
     * 
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Test
    public void testNotifyNodeAllocationCompleteSuccess() throws ServiceTimeoutException, ServiceExecutionException
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.cpsd.MessageProperties messageProperties =
                        new com.dell.cpsd.MessageProperties();
                messageProperties.setCorrelationId(UUID.randomUUID().toString());

                NodeAllocationInfo nodeAllocationInfo = new NodeAllocationInfo("elementIdentifier", "nodeIdentifier", AllocationStatus.ADDED);

                CompleteNodeAllocationResponseMessage responseMessage = new CompleteNodeAllocationResponseMessage(messageProperties, 
                        CompleteNodeAllocationResponseMessage.Status.SUCCESS, nodeAllocationInfo, Collections.emptyList());

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        Boolean responseInfo = nodeService.notifyNodeAllocationComplete("elementIdentifier");

        Assert.assertEquals(true, responseInfo);
        Mockito.verify(dneProducer, Mockito.times(1)).publishCompleteNodeAllocation(any());
    }
    
    /**
     * Test that the notifyNodeAllocationComplete method can handle any errors.
     * 
     * @throws ServiceTimeoutException
     */
    @Test(expected = ServiceExecutionException.class)
    public void testNotifyNodeAllocationCompleteError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
        nodeService.notifyNodeAllocationComplete("elementIdentifier");
        Mockito.verify(dneProducer, Mockito.times(1)).publishCompleteNodeAllocation(any());
    }

    /**
     * Test that the bootOrderStatus method can execute successfully.
     *
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Test
    public void testBootOrderStatusSuccess() throws ServiceTimeoutException, ServiceExecutionException
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        ConfigureBootDeviceIdracRequest bootOrderSequenceRequest = new ConfigureBootDeviceIdracRequest("nodeId", "idracIpAddress");

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.cpsd.MessageProperties messageProperties =
                        new com.dell.cpsd.MessageProperties(Calendar.getInstance().getTime(),
                                UUID.randomUUID().toString(), "replyToMe");

                List<ConfigureBootDeviceIdracError> configureBootDeviceIdracErrors = new ArrayList<ConfigureBootDeviceIdracError>();
                ConfigureBootDeviceIdracResponseMessage responseMessage = new ConfigureBootDeviceIdracResponseMessage(messageProperties, ConfigureBootDeviceIdracResponseMessage.Status.SUCCESS, configureBootDeviceIdracErrors );

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        BootDeviceIdracStatus bootOrderStatus = nodeService.bootDeviceIdracStatus(bootOrderSequenceRequest);

        Assert.assertNotNull(bootOrderStatus);
        Assert.assertEquals("SUCCESS", bootOrderStatus.getStatus());
//        Assert.assertEquals("", bootOrderStatus.getMessage());

        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureBootDeviceIdrac(any());
    }

    /**
     * Test that the bootOrderStatus method can handle any errors.
     *
     * @throws ServiceTimeoutException
     */
    @Test
    public void testBootOrderStatusError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);
        ConfigureBootDeviceIdracRequest bootOrderSequenceRequest = new ConfigureBootDeviceIdracRequest("nodeId", "idracIpAddress");
        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                                                  long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.bootDeviceIdracStatus(bootOrderSequenceRequest);
        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureBootDeviceIdrac(any());
    }

    /**
     * Test that the changeIdracCredentialsComplete method executes successfully.
     * 
     * @throws ServiceTimeoutException
     * @throws ServiceExecutionException
     */
    @Test
    public void testChangeIdracCredentialsCompleteSuccess() throws ServiceTimeoutException, ServiceExecutionException
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.cpsd.MessageProperties messageProperties =
                        new com.dell.cpsd.MessageProperties();
                messageProperties.setCorrelationId(UUID.randomUUID().toString());

                ChangeIdracCredentialsResponse changeCredentialsResponse = new ChangeIdracCredentialsResponse();
                changeCredentialsResponse.setNodeId("dummyNodeId");
                changeCredentialsResponse.setMessage("SUCCESS");

                ChangeIdracCredentialsResponseMessage responseMessage = new ChangeIdracCredentialsResponseMessage(messageProperties, 
                        ChangeIdracCredentialsResponseMessage.Status.SUCCESS, Collections.emptyList());

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        ChangeIdracCredentialsResponse responseInfo = nodeService.changeIdracCredentials("dummyNodeId");

        Assert.assertEquals("SUCCESS", responseInfo.getMessage());
        Mockito.verify(dneProducer, Mockito.times(1)).publishChangeIdracCredentials(any());
    }
    
    /**
     * Test that the changeIdracCredentialsComplete method can handle any errors.
     * 
     * @throws ServiceTimeoutException
     */
    @Test
    public void testChangeIdracCredentialsCompleteError() throws Exception
    {
        DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        DneProducer dneProducer = Mockito.mock(DneProducer.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", null,
                null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
        nodeService.changeIdracCredentials("dummyNodeId");
        Mockito.verify(dneProducer, Mockito.times(1)).publishChangeIdracCredentials(any());
    }

    @Test
    public void testListScaleIoComponentsFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestScaleIoComponents();
        Mockito.verify(dneProducer, Mockito.times(1)).publishListScaleIoComponents(any());
    }

    @Test
    public void testListScaleIoComponentsSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties(
                new Date(), UUID.randomUUID().toString(), "test");
        final ListComponentResponseMessage responseMessage = mock(ListComponentResponseMessage.class);
        final ScaleIOComponentDetails scaleIOComponentDetails = mock(ScaleIOComponentDetails.class);
        final ScaleIoEndpointDetails scaleIoEndpointDetails = mock(ScaleIoEndpointDetails.class);

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getComponents()).thenReturn(Arrays.asList(scaleIOComponentDetails));
        when(scaleIOComponentDetails.getEndpoints()).thenReturn(Arrays.asList(scaleIoEndpointDetails));
        when(scaleIoEndpointDetails.getCredentialUuids()).thenReturn(Arrays.asList("cred-uuid"));

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                responseMessage.setMessageProperties(messageProperties);
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(repository.saveScaleIoComponentDetails(anyList())).thenReturn(true);

        final boolean success = nodeService.requestScaleIoComponents();

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishListScaleIoComponents(any());
    }

    @Test
    public void testListVCenterComponentsFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestVCenterComponents();
        Mockito.verify(dneProducer, Mockito.times(1)).publishListVCenterComponents(any());
    }

    @Test
    public void testListVCenterComponentsSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");
        final ListComponentsResponseMessage responseMessage = mock(ListComponentsResponseMessage.class);
        final VCenterComponentDetails vCenterComponentDetails = mock(VCenterComponentDetails.class);
        final VCenterEndpointDetails vCenterEndpointDetails = mock(VCenterEndpointDetails.class);
        final VCenterCredentialDetails credentialDetail = mock(VCenterCredentialDetails.class);

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getVcenterComponentDetails()).thenReturn(Arrays.asList(vCenterComponentDetails));
        when(vCenterComponentDetails.getEndpoints()).thenReturn(Arrays.asList(vCenterEndpointDetails));
        when(vCenterEndpointDetails.getCredentialDetails()).thenReturn(Arrays.asList(credentialDetail));

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                responseMessage.setMessageProperties(messageProperties);
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(repository.saveVCenterComponentDetails(anyList())).thenReturn(true);

        final boolean success = nodeService.requestVCenterComponents();

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishListVCenterComponents(any());
    }

    @Test
    public void testDiscoverVCenterFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DiscoveryInfoToVCenterDomainTransformer transformer = Mockito.mock(DiscoveryInfoToVCenterDomainTransformer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = Mockito
                .mock(com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, transformer, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestDiscoverVCenter(componentEndpointIds, "job-id");
        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverVcenter(any());
    }

    @Test
    public void testDiscoverVCenterSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DiscoveryInfoToVCenterDomainTransformer transformer = Mockito.mock(DiscoveryInfoToVCenterDomainTransformer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = Mockito
                .mock(com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, transformer, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

                final DiscoveryResponseInfoMessage responseMessage = new DiscoveryResponseInfoMessage(messageProperties,
                        Arrays.asList(new Datacenter()));

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(transformer.transform(any())).thenReturn(new VCenter());
        when(repository.saveVCenterData(anyString(), any())).thenReturn(true);

        final boolean success = nodeService.requestDiscoverVCenter(componentEndpointIds, "job-id");

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverVcenter(any());
    }

    @Test
    public void testDiscoverScaleIoFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final ScaleIORestToScaleIODomainTransformer transformer = Mockito.mock(ScaleIORestToScaleIODomainTransformer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = Mockito
                .mock(com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, transformer,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestDiscoverScaleIo(componentEndpointIds, "job-id");
        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverScaleIo(any());
    }

    @Test
    public void testDiscoverScaleIoSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final ScaleIORestToScaleIODomainTransformer transformer = Mockito.mock(ScaleIORestToScaleIODomainTransformer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds componentEndpointIds = Mockito
                .mock(com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, transformer,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final com.dell.cpsd.storage.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.storage.capabilities.api.MessageProperties(
                        new Date(), UUID.randomUUID().toString(), "test");

                final ListStorageResponseMessage responseMessage = new ListStorageResponseMessage(messageProperties,
                        new ScaleIOSystemDataRestRep());

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(transformer.transform(any())).thenReturn(new ScaleIOData());
        when(repository.saveScaleIoData(anyString(), any())).thenReturn(true);

        final boolean success = nodeService.requestDiscoverScaleIo(componentEndpointIds, "job-id");

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverScaleIo(any());
    }

    @Test
    public void testApplyEsxiLicenseFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final AddEsxiHostVSphereLicenseRequest request = Mockito.mock(AddEsxiHostVSphereLicenseRequest.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestInstallEsxiLicense(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishApplyEsxiLicense(request);
    }

    @Test
    public void testApplyEsxiLicenseSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final AddEsxiHostVSphereLicenseRequest request = Mockito.mock(AddEsxiHostVSphereLicenseRequest.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(
                        new Date(), UUID.randomUUID().toString(), "test");

                final AddEsxiHostVSphereLicenseResponse responseMessage = new AddEsxiHostVSphereLicenseResponse(messageProperties,
                        AddEsxiHostVSphereLicenseResponse.Status.SUCCESS);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestInstallEsxiLicense(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishApplyEsxiLicense(request);
    }

    @Test
    public void testExitHostMaintenanceFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final HostMaintenanceModeRequestMessage request = mock(HostMaintenanceModeRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestExitHostMaintenanceMode(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishEsxiHostExitMaintenanceMode(request);
    }

    @Test
    public void testExitHostMaintenanceSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final HostMaintenanceModeRequestMessage request = Mockito.mock(HostMaintenanceModeRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

                final HostMaintenanceModeResponseMessage responseMessage = new HostMaintenanceModeResponseMessage(messageProperties,
                        HostMaintenanceModeResponseMessage.Status.SUCCESS);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestExitHostMaintenanceMode(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishEsxiHostExitMaintenanceMode(request);
    }

    @Test
    public void testInstallEsxiFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final EsxiInstallationInfo info = mock(EsxiInstallationInfo.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestInstallEsxi(info);
        Mockito.verify(dneProducer, Mockito.times(1)).publishInstallEsxiRequest(any());
    }

    @Test
    public void testInstallEsxiSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final InstallESXiResponseMessage responseMessage = mock(InstallESXiResponseMessage.class);
        final com.dell.cpsd.MessageProperties messageProperties = new com.dell.cpsd.MessageProperties(
                new Date(), UUID.randomUUID().toString(), "test");

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn("FINISHED");

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestInstallEsxi(any());

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishInstallEsxiRequest(any());
    }

    @Test
    public void testAddHostToVCenterFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final ClusterOperationRequestMessage request = mock(ClusterOperationRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestAddHostToVCenter(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishAddHostToVCenter(request);
    }

    @Test
    public void testAddHostToVCenterSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final ClusterOperationRequestMessage request = Mockito.mock(ClusterOperationRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

                final ClusterOperationResponseMessage responseMessage = new ClusterOperationResponseMessage(messageProperties,
                        ClusterOperationResponseMessage.Status.SUCCESS);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestAddHostToVCenter(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishAddHostToVCenter(request);
    }

    @Test
    public void testAddHostToDvSwitchFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final AddHostToDvSwitchRequestMessage request = mock(AddHostToDvSwitchRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestAddHostToDvSwitch(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishAddHostToDvSwitch(request);
    }

    @Test
    public void testAddHostToDvSwitchSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final AddHostToDvSwitchRequestMessage request = Mockito.mock(AddHostToDvSwitchRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

                final AddHostToDvSwitchResponseMessage responseMessage = new AddHostToDvSwitchResponseMessage(messageProperties,
                        AddHostToDvSwitchResponseMessage.Status.SUCCESS);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestAddHostToDvSwitch(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishAddHostToDvSwitch(request);
    }

    @Test
    public void testDeployScaleIoVmFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final DeployVMFromTemplateRequestMessage request = mock(DeployVMFromTemplateRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestDeployScaleIoVm(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishDeployVmFromTemplate(request);
    }

    @Test
    public void testDeployScaleIoVmSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = Mockito.mock(DneProducer.class);
        final DataServiceRepository repository = Mockito.mock(DataServiceRepository.class);
        final DeployVMFromTemplateRequestMessage request = Mockito.mock(DeployVMFromTemplateRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

                final DeployVMFromTemplateResponseMessage responseMessage = new DeployVMFromTemplateResponseMessage(messageProperties,
                        DeployVMFromTemplateResponseMessage.Status.SUCCESS);

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        final boolean success = nodeService.requestDeployScaleIoVm(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishDeployVmFromTemplate(request);
    }

    @Test
    public void testEnablePciPassThroughFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final EnablePCIPassthroughRequestMessage request = mock(EnablePCIPassthroughRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestEnablePciPassThrough(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishEnablePciPassthrough(request);
    }

    @Test
    public void testEnablePciPassThroughSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final EnablePCIPassthroughRequestMessage request = mock(EnablePCIPassthroughRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final EnablePCIPassthroughResponseMessage responseMessage = mock(EnablePCIPassthroughResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn(EnablePCIPassthroughResponseMessage.Status.SUCCESS_REBOOT_REQUIRED);

        final boolean success = nodeService.requestEnablePciPassThrough(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishEnablePciPassthrough(request);
    }

    @Test
    public void testRebootHostFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final HostPowerOperationRequestMessage request = mock(HostPowerOperationRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestHostReboot(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishRebootHost(request);
    }

    @Test
    public void testRebootHostSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final HostPowerOperationRequestMessage request = mock(HostPowerOperationRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final HostPowerOperationResponseMessage responseMessage = mock(HostPowerOperationResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn(HostPowerOperationResponseMessage.Status.SUCCESS);

        final boolean success = nodeService.requestHostReboot(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishRebootHost(request);
    }

    @Test
    public void testConfigurePciPassThroughFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final UpdatePCIPassthruSVMRequestMessage request = mock(UpdatePCIPassthruSVMRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestSetPciPassThrough(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishSetPciPassthrough(request);
    }

    @Test
    public void testConfigurePciPassThroughSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final UpdatePCIPassthruSVMRequestMessage request = mock(UpdatePCIPassthruSVMRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final UpdatePCIPassthruSVMResponseMessage responseMessage = mock(UpdatePCIPassthruSVMResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn(UpdatePCIPassthruSVMResponseMessage.Status.SUCCESS);

        final boolean success = nodeService.requestSetPciPassThrough(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishSetPciPassthrough(request);
    }

    @Test
    public void testConfigureSoftwareVibFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final SoftwareVIBConfigureRequestMessage request = mock(SoftwareVIBConfigureRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestConfigureScaleIoVib(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureScaleIoVib(request);
    }

    @Test
    public void testConfigureSoftwareVibSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final SoftwareVIBConfigureRequestMessage request = mock(SoftwareVIBConfigureRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final SoftwareVIBResponseMessage responseMessage = mock(SoftwareVIBResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn(SoftwareVIBResponseMessage.Status.SUCCESS);

        final boolean success = nodeService.requestConfigureScaleIoVib(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureScaleIoVib(request);
    }

    @Test
    public void testInstallSoftwareVibFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final SoftwareVIBRequestMessage request = mock(SoftwareVIBRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.requestInstallSoftwareVib(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishInstallScaleIoVib(request);
    }

    @Test
    public void testInstallSoftwareVibSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final SoftwareVIBRequestMessage request = mock(SoftwareVIBRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final SoftwareVIBResponseMessage responseMessage = mock(SoftwareVIBResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getStatus()).thenReturn(SoftwareVIBResponseMessage.Status.SUCCESS);

        final boolean success = nodeService.requestInstallSoftwareVib(request);

        assertTrue(success);

        Mockito.verify(dneProducer, Mockito.times(1)).publishInstallScaleIoVib(request);
    }

    @Test
    public void testListEsxiDefaultCredentialDetailsFailure() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final ListEsxiCredentialDetailsRequestMessage request = mock(ListEsxiCredentialDetailsRequestMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.listDefaultCredentials(request);
        Mockito.verify(dneProducer, Mockito.times(1)).publishListExsiCredentialDetails(request);
    }

    @Test
    public void testListEsxiDefaultCredentialDetailsSuccess() throws Exception
    {
        final DelegatingMessageConsumer consumer = new DefaultMessageConsumer();
        final DneProducer dneProducer = mock(DneProducer.class);
        final DataServiceRepository repository = mock(DataServiceRepository.class);
        final ListEsxiCredentialDetailsRequestMessage request = mock(ListEsxiCredentialDetailsRequestMessage.class);
        final MessageProperties messageProperties = new MessageProperties(new Date(), UUID.randomUUID().toString(), "test");

        final ListEsxiCredentialDetailsResponseMessage responseMessage = mock(ListEsxiCredentialDetailsResponseMessage.class);

        AmqpNodeService nodeService = new AmqpNodeService(null, consumer, dneProducer, "replyToMe", repository, null, null,null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        when(responseMessage.getMessageProperties()).thenReturn(messageProperties);
        when(responseMessage.getComponentUuid()).thenReturn("comp-uuid");
        when(responseMessage.getEndpointUuid()).thenReturn("end-uuid");
        when(responseMessage.getCredentialUuid()).thenReturn("cred-uuid");

        final ComponentEndpointIds componentEndpointIds = nodeService.listDefaultCredentials(request);

        assertEquals(componentEndpointIds.getComponentUuid(), "comp-uuid");
        assertEquals(componentEndpointIds.getEndpointUuid(), "end-uuid");
        assertEquals(componentEndpointIds.getCredentialUuid(), "cred-uuid");

        Mockito.verify(dneProducer, Mockito.times(1)).publishListExsiCredentialDetails(request);
    }
}
