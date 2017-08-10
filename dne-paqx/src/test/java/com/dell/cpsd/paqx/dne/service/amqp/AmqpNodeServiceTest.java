/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponse;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.dell.converged.capabilities.compute.discovered.nodes.api.ChangeIdracCredentialsResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.CompleteNodeAllocationResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracError;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.NodeAllocationInfo;
import com.dell.converged.capabilities.compute.discovered.nodes.api.NodeAllocationInfo.AllocationStatus;
import com.dell.converged.capabilities.compute.discovered.nodes.api.NodesListed;
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
                null, null)
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                // default status of discovered
                com.dell.converged.capabilities.compute.discovered.nodes.api.DiscoveredNode node =
                        new com.dell.converged.capabilities.compute.discovered.nodes.api.DiscoveredNode(nodeId, convergedUuid, 
                                com.dell.converged.capabilities.compute.discovered.nodes.api.DiscoveredNode.AllocationStatus.DISCOVERED);

                com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties messageProperties =
                        new com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties(Calendar.getInstance().getTime(),
                                UUID.randomUUID().toString(), "replyToMe");

                NodesListed listed = new NodesListed(messageProperties, Arrays.asList(node));
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, listed, null));
            }
        };

        List<DiscoveredNode> discovereds = nodeService.listDiscoveredNodes();
        Assert.assertEquals(1, discovereds.size());

        DiscoveredNode discovered = discovereds.get(0);
        Assert.assertEquals(convergedUuid, discovered.getConvergedUuid());
        Assert.assertEquals(nodeId, discovered.getNodeId());

        Mockito.verify(dneProducer, Mockito.times(1)).publishListNodes(Mockito.any());
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
                null, null)
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
                Mockito.mock(DataServiceRepository.class), null, null)
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
                Mockito.mock(DataServiceRepository.class), null, null)
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
                        new DiscoverClusterResponseInfoMessage(messageProperties, responseInfo);
                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseInfoMessage, null));
            }
        };



        List<ClusterInfo> discovereds = nodeService.listClusters();
        Assert.assertEquals(1, discovereds.size());

        ClusterInfo discovered = discovereds.get(0);
        Assert.assertEquals(clusterName, discovered.getName());
        Assert.assertEquals(numberOfHosts, discovered.getNumberOfHosts());

        Mockito.verify(dneProducer, Mockito.times(1)).publishDiscoverClusters(Mockito.any());
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
                null, null)
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
                null, null)
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

        Mockito.verify(dneProducer, Mockito.times(1)).publishIdracNetwokSettings(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
       	nodeService.idracNetworkSettings(idracNetworkSettingsRequest);
       	Mockito.verify(dneProducer, Mockito.times(1)).publishIdracNetwokSettings(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties messageProperties = 
                        new com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties();
                messageProperties.setCorrelationId(UUID.randomUUID().toString());

                NodeAllocationInfo nodeAllocationInfo = new NodeAllocationInfo("elementIdentifier", "nodeIdentifier", AllocationStatus.ADDED);

                CompleteNodeAllocationResponseMessage responseMessage = new CompleteNodeAllocationResponseMessage(messageProperties, 
                        CompleteNodeAllocationResponseMessage.Status.SUCCESS, nodeAllocationInfo, Collections.emptyList());

                serviceCallback.handleServiceResponse(new ServiceResponse<>(requestId, responseMessage, null));
            }
        };

        Boolean responseInfo = nodeService.notifyNodeAllocationComplete("elementIdentifier");

        Assert.assertEquals(true, responseInfo);
        Mockito.verify(dneProducer, Mockito.times(1)).publishCompleteNodeAllocation(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
        nodeService.notifyNodeAllocationComplete("elementIdentifier");
        Mockito.verify(dneProducer, Mockito.times(1)).publishCompleteNodeAllocation(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties messageProperties =
                        new com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties(Calendar.getInstance().getTime(),
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

        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureBootDeviceIdrac(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                                                  long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };

        nodeService.bootDeviceIdracStatus(bootOrderSequenceRequest);
        Mockito.verify(dneProducer, Mockito.times(1)).publishConfigureBootDeviceIdrac(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId, long timeout)
                    throws ServiceTimeoutException
            {
                com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties messageProperties = 
                        new com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties();
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
        Mockito.verify(dneProducer, Mockito.times(1)).publishChangeIdracCredentials(Mockito.any());
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
                null, null)
        {
            @Override
            protected void waitForServiceCallback(ServiceCallback serviceCallback, String requestId,
                    long timeout) throws ServiceTimeoutException
            {
                serviceCallback.handleServiceError(new ServiceError(requestId, "network", "network"));
            }
        };
        
        nodeService.changeIdracCredentials("dummyNodeId");
        Mockito.verify(dneProducer, Mockito.times(1)).publishChangeIdracCredentials(Mockito.any());
    }

}
