/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp;

import com.dell.converged.capabilities.compute.discovered.nodes.api.MessageProperties;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ChangeIdracCredentialsRequestMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ChangeIdracCredentialsResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.CompleteNodeAllocationRequestMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.CompleteNodeAllocationResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracError;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracRequestMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ConfigureBootDeviceIdracResponseMessage;
import com.dell.converged.capabilities.compute.discovered.nodes.api.ListNodes;
import com.dell.converged.capabilities.compute.discovered.nodes.api.NodesListed;
import com.dell.cpsd.common.logging.ILogger;
import com.dell.cpsd.paqx.dne.amqp.producer.DneProducer;
import com.dell.cpsd.paqx.dne.repository.DataServiceRepository;
import com.dell.cpsd.paqx.dne.service.NodeService;
import com.dell.cpsd.paqx.dne.service.amqp.adapter.*;
import com.dell.cpsd.paqx.dne.service.model.BootDeviceIdracStatus;
import com.dell.cpsd.paqx.dne.service.model.ChangeIdracCredentialsResponse;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointDetails;
import com.dell.cpsd.paqx.dne.service.model.ComponentEndpointIds;
import com.dell.cpsd.paqx.dne.service.model.ConfigureBootDeviceIdracRequest;
import com.dell.cpsd.paqx.dne.service.model.DiscoverScaleIoTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.DiscoverVCenterTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.DiscoveredNode;
import com.dell.cpsd.paqx.dne.service.model.EndpointCredentials;
import com.dell.cpsd.paqx.dne.service.model.IdracInfo;
import com.dell.cpsd.paqx.dne.service.model.IdracNetworkSettingsRequest;
import com.dell.cpsd.paqx.dne.service.model.ListScaleIoComponentsTaskResponse;
import com.dell.cpsd.paqx.dne.service.model.ListVCenterComponentsTaskResponse;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettings;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsRequestMessage;
import com.dell.cpsd.rackhd.adapter.model.idrac.IdracNetworkSettingsResponseMessage;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.exception.ServiceExecutionException;
import com.dell.cpsd.service.common.client.exception.ServiceTimeoutException;
import com.dell.cpsd.service.common.client.rpc.AbstractServiceClient;
import com.dell.cpsd.service.common.client.rpc.DelegatingMessageConsumer;
import com.dell.cpsd.service.common.client.rpc.ServiceRequestCallback;
import com.dell.cpsd.virtualization.capabilities.api.*;
import com.dell.cpsd.storage.capabilities.api.ListComponentRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageRequestMessage;
import com.dell.cpsd.storage.capabilities.api.ListStorageResponseMessage;
import com.dell.cpsd.storage.capabilities.api.ScaleIOComponentDetails;
import com.dell.cpsd.storage.capabilities.api.ScaleIoEndpointDetails;
import com.dell.cpsd.virtualization.capabilities.api.ClusterInfo;
import com.dell.cpsd.virtualization.capabilities.api.Credentials;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfo;
import com.dell.cpsd.virtualization.capabilities.api.DiscoverClusterResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryRequestInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsRequestMessage;
import com.dell.cpsd.virtualization.capabilities.api.ListComponentsResponseMessage;
import com.dell.cpsd.virtualization.capabilities.api.VCenterComponentDetails;
import com.dell.cpsd.virtualization.capabilities.api.VCenterEndpointDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @since 1.0
 */
public class AmqpNodeService extends AbstractServiceClient implements NodeService
{
    /*
     * The logger instance
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpNodeService.class);

    /*
     * The <code>DelegatingMessageConsumer</code>
     */
    private final DelegatingMessageConsumer consumer;

    /*
     * The <code>DneProducer</code>
     */
    private final DneProducer producer;

    /*
     * The replyTo queue name
     */
    private final String replyTo;
    private final long timeout = 600000L;

    private final DataServiceRepository repository;

    /**
     * AmqpNodeService constructor.
     *
     * @param logger   - The logger instance.
     * @param consumer - The <code>DelegatingMessageConsumer</code> instance.
     * @param producer - The <code>DneProducer</code> instance.
     * @param replyTo  - The replyTo queue name.
     * @param repository
     * @since 1.0
     */
    public AmqpNodeService(ILogger logger, DelegatingMessageConsumer consumer, DneProducer producer, String replyTo,
            final DataServiceRepository repository)
    {
        super(logger);

        this.consumer = consumer;
        this.producer = producer;
        this.replyTo = replyTo;
        this.repository = repository;

        initCallbacks();
    }

    /*
     * Initialize message consumer adapters.
     *
     * @since 1.0
     */
    private void initCallbacks()
    {
        this.consumer.addAdapter(new NodesListedResponseAdapter(this));
        this.consumer.addAdapter(new ClustersListedResponseAdapter(this));
        this.consumer.addAdapter(new CompleteNodeAllocationResponseAdapter(this));
        this.consumer.addAdapter(new IdracConfigResponseAdapter(this));
        this.consumer.addAdapter(new ChangeIdracCredentialsResponseAdapter(this));
        this.consumer.addAdapter(new ConfigureBootDeviceIdracResponseAdapter(this));
        this.consumer.addAdapter(new ValidateClusterResponseAdapter(this));
        this.consumer.addAdapter(new ListScaleIoComponentsResponseAdapter(this));
        this.consumer.addAdapter(new ListVCenterComponentsResponseAdapter(this));
        this.consumer.addAdapter(new DiscoverScaleIoResponseAdapter(this));
        this.consumer.addAdapter(new DiscoverVCenterResponseAdapter(this));
        this.consumer.addAdapter(new InstallEsxiResponseAdapter(this));
        this.consumer.addAdapter(new AddHostToVCenterResponseAdapter(this));
        this.consumer.addAdapter(new SoftwareVibResponseAdapter(this));
        this.consumer.addAdapter(new AddHostToDvSwitchResponseAdapter(this));
        this.consumer.addAdapter(new DeployScaleIoVmResponseAdapter(this));
        this.consumer.addAdapter(new EnablePciPassthroughResponseAdapter(this));
        this.consumer.addAdapter(new RebootHostResponseAdapter(this));
        this.consumer.addAdapter(new SetPciPassthroughResponseAdapter(this));
        this.consumer.addAdapter(new ApplyEsxiLicenseResponseAdapter(this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IdracInfo idracNetworkSettings(IdracNetworkSettingsRequest idracNetworkSettingsRequest)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        IdracInfo idracInfo = new IdracInfo();

        try
        {
            IdracNetworkSettingsRequestMessage idracNetworkSettingsRequestMessage = new IdracNetworkSettingsRequestMessage();
            com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties messageProperties = new com.dell.cpsd.rackhd.adapter.rabbitmq.MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            idracNetworkSettingsRequestMessage.setMessageProperties(messageProperties);

            IdracNetworkSettings idracNetworkSettings = new IdracNetworkSettings();

            idracNetworkSettings.setNodeId(idracNetworkSettingsRequest.getNodeId());
            idracNetworkSettings.setIpAddress(idracNetworkSettingsRequest.getIdracIpAddress());
            idracNetworkSettings.setGateway(idracNetworkSettingsRequest.getIdracGatewayIpAddress());
            idracNetworkSettings.setNetmask(idracNetworkSettingsRequest.getIdracSubnetMask());

            idracNetworkSettingsRequestMessage.setIdracNetworkSettings(idracNetworkSettings);

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishIdracNetwokSettings(idracNetworkSettingsRequestMessage);
                }
            });
            IdracNetworkSettingsResponseMessage resp = processResponse(response, IdracNetworkSettingsResponseMessage.class);

            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getIdracNetworkSettingsResponse() != null)
                    {
                        LOGGER.info("Response from amqp ipAddress: " + resp.getIdracNetworkSettingsResponse().getIpAddress());
                        LOGGER.info("Response from amqp subnet: " + resp.getIdracNetworkSettingsResponse().getNetmask());
                        LOGGER.info("Response from amqp gateway: " + resp.getIdracNetworkSettingsResponse().getGateway());
                        LOGGER.info("Response from amqp nodeId: " + resp.getIdracNetworkSettingsResponse().getNodeId());

                        if ("SUCCESS".equalsIgnoreCase(resp.getIdracNetworkSettingsResponse().getMessage()))
                        {
                            idracInfo.setIdracIpAddress(resp.getIdracNetworkSettingsResponse().getIpAddress());
                            idracInfo.setIdracSubnetMask(resp.getIdracNetworkSettingsResponse().getNetmask());
                            idracInfo.setIdracGatewayIpAddress(resp.getIdracNetworkSettingsResponse().getGateway());
                            idracInfo.setNodeId(resp.getIdracNetworkSettingsResponse().getNodeId());

                        }
                        else
                        {
                            LOGGER.error(
                                    "Error response from configure idrac settings: " + resp.getIdracNetworkSettingsResponse().getMessage());
                        }
                        idracInfo.setMessage(resp.getIdracNetworkSettingsResponse().getMessage());
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in idracNetworkSettings: ", e);
        }

        return idracInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DiscoveredNode> listDiscoveredNodes() throws ServiceTimeoutException, ServiceExecutionException
    {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        ListNodes request = new ListNodes(messageProperties, Collections.emptyList());
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishListNodes(request);
            }
        });

        NodesListed nodes = processResponse(response, NodesListed.class);
        if (nodes != null)
        {
            if (nodes.getDiscoveredNodes() != null)
            {
                return nodes.getDiscoveredNodes().stream()
                        .map(d -> new DiscoveredNode(d.getConvergedUuid(), d.getNodeId(), d.getAllocationStatus()))
                        .collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ClusterInfo> listClusters() throws ServiceTimeoutException, ServiceExecutionException
    {
        com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        DiscoverClusterRequestInfoMessage request = new DiscoverClusterRequestInfoMessage();
        request.setMessageProperties(messageProperties);
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish list cluster request message");
                producer.publishDiscoverClusters(request);
            }
        });

        DiscoverClusterResponseInfoMessage responseInfo = processResponse(response, DiscoverClusterResponseInfoMessage.class);
        if (responseInfo != null)
        {
            DiscoverClusterResponseInfo clusterResponseInfo = responseInfo.getDiscoverClusterResponseInfo();
            return clusterResponseInfo != null? clusterResponseInfo.getClusters(): Collections.emptyList();
        }

        return Collections.emptyList();
    }

    @Override
    public ValidateVcenterClusterResponseMessage validateClusters(List<ClusterInfo> clusterInfoList) throws ServiceTimeoutException, ServiceExecutionException {
        com.dell.cpsd.virtualization.capabilities.api.MessageProperties messageProperties = new com.dell.cpsd.virtualization.capabilities.api.MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        ValidateVcenterClusterRequestMessage request = new ValidateVcenterClusterRequestMessage();
        request.setMessageProperties(messageProperties);
        request.setDiscoverClusterResponseInfo(new DiscoverClusterResponseInfo(clusterInfoList));
        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                LOGGER.info("publish validate cluster request message");
                producer.publishValidateClusters(request);
            }
        });

        ValidateVcenterClusterResponseMessage responseInfo = processResponse(response, ValidateVcenterClusterResponseMessage.class);
        return responseInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean notifyNodeAllocationComplete(String elementIdentifier) throws ServiceTimeoutException, ServiceExecutionException
    {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setCorrelationId(UUID.randomUUID().toString());
        messageProperties.setTimestamp(Calendar.getInstance().getTime());
        messageProperties.setReplyTo(replyTo);

        CompleteNodeAllocationRequestMessage request = new CompleteNodeAllocationRequestMessage(messageProperties, elementIdentifier);

        ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
        {
            @Override
            public String getRequestId()
            {
                return messageProperties.getCorrelationId();
            }

            @Override
            public void executeRequest(String requestId) throws Exception
            {
                producer.publishCompleteNodeAllocation(request);
            }
        });

        CompleteNodeAllocationResponseMessage responseInfo = processResponse(response, CompleteNodeAllocationResponseMessage.class);

        if (CompleteNodeAllocationResponseMessage.Status.FAILED.equals(responseInfo.getStatus()))
        {
            LOGGER.error("Error response from notify node allocation complete: " + responseInfo.getNodeAllocationErrors());
        }

        return CompleteNodeAllocationResponseMessage.Status.SUCCESS.equals(responseInfo.getStatus());
    }

    /**
     * Process a RPC response message.
     *
     * @param response         - The <code>ServiceResponse</code> to process.
     * @param expectedResponse - The expected response <code>Class</code>
     * @return The response.
     * @throws ServiceExecutionException
     * @since 1.0
     */
    private <R> R processResponse(ServiceResponse<?> response, Class<R> expectedResponse) throws ServiceExecutionException
    {
        Object responseMessage = response.getResponse();
        if (responseMessage == null)
        {
            return null;
        }

        if (expectedResponse.isAssignableFrom(responseMessage.getClass()))
        {
            return (R) responseMessage;
        }
        else
        {
            throw new UnsupportedOperationException("Unexpected response message: " + responseMessage);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeIdracCredentialsResponse changeIdracCredentials(String nodeId) throws ServiceTimeoutException, ServiceExecutionException
    {
        ChangeIdracCredentialsResponse responseMessage = new ChangeIdracCredentialsResponse();

        try
        {
            ChangeIdracCredentialsRequestMessage changeIdracCredentialsRequestMessage = new ChangeIdracCredentialsRequestMessage();
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);

            changeIdracCredentialsRequestMessage.setMessageProperties(messageProperties);
            changeIdracCredentialsRequestMessage.setNodeID(nodeId);

            LOGGER.info("Sending Change Idrac Credentials request with correlation id: " + messageProperties.getCorrelationId());

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishChangeIdracCredentials(changeIdracCredentialsRequestMessage);
                }
            });

            ChangeIdracCredentialsResponseMessage resp = processResponse(response, ChangeIdracCredentialsResponseMessage.class);

            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getStatus() != null)
                    {
                        LOGGER.info("Response for Change Idrac Credentials: " + resp.getStatus());
                        responseMessage.setNodeId(nodeId);

                        if ("SUCCESS".equalsIgnoreCase(resp.getStatus().toString()))
                        {
                            responseMessage.setMessage("SUCCESS");
                        }
                        else
                        {
                            LOGGER.error("Error response from change idrac credentials: " + resp.getChangeIdracCredentialsErrors());
                            responseMessage.setMessage("Error while setting new credentials to the node " + nodeId);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in change idrac credentials: ", e);
        }

        return responseMessage;
    }

    @Override
    public BootDeviceIdracStatus bootDeviceIdracStatus(ConfigureBootDeviceIdracRequest configureBootDeviceIdracRequest)
            throws ServiceTimeoutException, ServiceExecutionException
    {

        BootDeviceIdracStatus bootDeviceIdracStatus = new BootDeviceIdracStatus();

        try
        {
            ConfigureBootDeviceIdracRequestMessage configureBootDeviceIdracRequestMessage = new ConfigureBootDeviceIdracRequestMessage();

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setCorrelationId(UUID.randomUUID().toString());
            messageProperties.setTimestamp(Calendar.getInstance().getTime());
            messageProperties.setReplyTo(replyTo);
            configureBootDeviceIdracRequestMessage.setMessageProperties(messageProperties);

            configureBootDeviceIdracRequestMessage.setNodeID(configureBootDeviceIdracRequest.getNodeId());
            configureBootDeviceIdracRequestMessage.setIpAddress(configureBootDeviceIdracRequest.getIdracIpAddress());

            ServiceResponse<?> response = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return messageProperties.getCorrelationId();
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishConfigureBootDeviceIdrac(configureBootDeviceIdracRequestMessage);
                }
            });

            ConfigureBootDeviceIdracResponseMessage resp = processResponse(response, ConfigureBootDeviceIdracResponseMessage.class);
            if (resp != null)
            {
                if (resp.getMessageProperties() != null)
                {
                    if (resp.getStatus() != null)
                    {
                        LOGGER.info("Response message is: " + resp.getStatus().toString());

                        bootDeviceIdracStatus.setStatus(resp.getStatus().toString());
                        List<ConfigureBootDeviceIdracError> errors = resp.getConfigureBootDeviceIdracErrors();
                        if (!CollectionUtils.isEmpty(errors))
                        {
                            List<String> errorMsgs = new ArrayList<String>();
                            for (ConfigureBootDeviceIdracError error : errors)
                            {
                                errorMsgs.add(error.getMessage());
                            }
                            bootDeviceIdracStatus.setErrors(errorMsgs);
                        }
                    }
                }

            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception in boot order sequence: ", e);
        }
        return bootDeviceIdracStatus;
    }

    @Override
    public ListScaleIoComponentsTaskResponse requestScaleIoComponents() throws ServiceTimeoutException, ServiceExecutionException
    {
        final ListScaleIoComponentsTaskResponse taskResponse = new ListScaleIoComponentsTaskResponse();

        try
        {
            final ListComponentRequestMessage requestMessage = new ListComponentRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage
                    .setMessageProperties(new com.dell.cpsd.storage.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishListScaleIoComponents(requestMessage);
                }
            });

            ListComponentResponseMessage responseMessage = processResponse(callbackResponse, ListComponentResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                taskResponse.setType(responseMessage.getType());
                taskResponse.setMessage("SUCCESS");

                final List<ScaleIOComponentDetails> scaleIOComponentDetailsList = responseMessage.getComponents();

                if (scaleIOComponentDetailsList != null)
                {
                    final List<ComponentEndpointDetails> componentEndpointDetailsList = new ArrayList<>();

                    scaleIOComponentDetailsList.stream().filter(Objects::nonNull).forEach(scaleIOComponentDetails -> {

                        final ComponentEndpointDetails componentEndpointDetails = new ComponentEndpointDetails();

                        componentEndpointDetails.setComponentUuid(scaleIOComponentDetails.getComponentUuid());
                        componentEndpointDetails.setElementType(scaleIOComponentDetails.getElementType());

                        final List<ScaleIoEndpointDetails> endpointDetailsList = scaleIOComponentDetails.getEndpoints();

                        if (endpointDetailsList != null)
                        {
                            endpointDetailsList.stream().filter(Objects::nonNull).forEach(scaleIoEndpointDetails -> {
                                final EndpointCredentials endpointCredentials = new EndpointCredentials();
                                endpointCredentials.setEndpointUrl(scaleIoEndpointDetails.getEndpointUrl());
                                endpointCredentials.setEndpointUuid(scaleIoEndpointDetails.getEndpointUuid());
                                endpointCredentials.setCredentialUuids(scaleIoEndpointDetails.getCredentialUuids());
                                componentEndpointDetails.getEndpointCredentials().add(endpointCredentials);
                            });
                        }

                        componentEndpointDetailsList.add(componentEndpointDetails);

                    });

                    taskResponse.setComponentEndpointDetails(componentEndpointDetailsList);

                    repository.saveScaleIoComponentDetails(componentEndpointDetailsList);
                }
            }
            else
            {
                LOGGER.error("Response was null");
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return taskResponse;
    }

    @Override
    public ListVCenterComponentsTaskResponse requestVCenterComponents() throws ServiceTimeoutException, ServiceExecutionException
    {
        final ListVCenterComponentsTaskResponse taskResponse = new ListVCenterComponentsTaskResponse();

        try
        {
            final ListComponentsRequestMessage requestMessage = new ListComponentsRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage
                    .setMessageProperties(new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishListVCenterComponents(requestMessage);
                }
            });

            ListComponentsResponseMessage responseMessage = processResponse(callbackResponse, ListComponentsResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                taskResponse.setType(responseMessage.getType());
                taskResponse.setMessage("SUCCESS");

                final List<VCenterComponentDetails> vCenterComponentDetailsList = responseMessage.getVcenterComponentDetails();

                if (vCenterComponentDetailsList != null)
                {
                    final List<ComponentEndpointDetails> componentEndpointDetailsList = new ArrayList<>();

                    vCenterComponentDetailsList.stream().filter(Objects::nonNull).forEach(vcenterComponentDetails -> {

                        final ComponentEndpointDetails componentEndpointDetails = new ComponentEndpointDetails();

                        componentEndpointDetails.setComponentUuid(vcenterComponentDetails.getComponentUuid());
                        componentEndpointDetails.setElementType(vcenterComponentDetails.getElementType());

                        final List<VCenterEndpointDetails> endpointDetailsList = vcenterComponentDetails.getEndpoints();

                        if (endpointDetailsList != null)
                        {
                            endpointDetailsList.stream().filter(Objects::nonNull).forEach(vcenterEndpointDetails -> {
                                final EndpointCredentials endpointCredentials = new EndpointCredentials();
                                endpointCredentials.setEndpointUrl(vcenterEndpointDetails.getEndpointUrl());
                                endpointCredentials.setEndpointUuid(vcenterEndpointDetails.getEndpointUuid());
                                endpointCredentials.setCredentialUuids(vcenterEndpointDetails.getCredentialUuids());
                                componentEndpointDetails.getEndpointCredentials().add(endpointCredentials);
                            });
                        }

                        componentEndpointDetailsList.add(componentEndpointDetails);

                    });

                    taskResponse.setComponentEndpointDetails(componentEndpointDetailsList);

                    repository.saveVCenterComponentDetails(componentEndpointDetailsList);
                }
            }
            else
            {
                LOGGER.error("Response was null");
            }

        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return taskResponse;
    }

    @Override
    public DiscoverScaleIoTaskResponse requestDiscoverScaleIo(final ComponentEndpointIds componentEndpointIds)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        final DiscoverScaleIoTaskResponse taskResponse = new DiscoverScaleIoTaskResponse();

        try
        {
            final ListStorageRequestMessage requestMessage = new ListStorageRequestMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.storage.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));
            requestMessage.setEndpointURL(componentEndpointIds.getEndpointUrl());
            requestMessage.setComponentUuid(componentEndpointIds.getComponentUuid());
            requestMessage.setEndpointUuid(componentEndpointIds.getEndpointUuid());
            requestMessage.setCredentialUuid(componentEndpointIds.getCredentialUuid());

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDiscoverScaleIo(requestMessage);
                }
            });

            ListStorageResponseMessage responseMessage = processResponse(callbackResponse, ListStorageResponseMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties()!= null)
            {
                //TODO: Send some data and persist it
                repository.saveScaleIoData();
            }
            else
            {
                LOGGER.error("Message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return taskResponse;
    }

    @Override
    public DiscoverVCenterTaskResponse requestDiscoverVCenter(final ComponentEndpointIds componentEndpointIds)
            throws ServiceTimeoutException, ServiceExecutionException
    {
        final DiscoverVCenterTaskResponse taskResponse = new DiscoverVCenterTaskResponse();

        try
        {
            final DiscoveryRequestInfoMessage requestMessage = new DiscoveryRequestInfoMessage();
            final String correlationId = UUID.randomUUID().toString();
            requestMessage.setMessageProperties(
                    new com.dell.cpsd.virtualization.capabilities.api.MessageProperties(new Date(), correlationId, replyTo));
            requestMessage.setCredentials(new Credentials(componentEndpointIds.getEndpointUrl(), null, null));
            requestMessage.setComponentEndpointIds(
                    new com.dell.cpsd.virtualization.capabilities.api.ComponentEndpointIds(componentEndpointIds.getComponentUuid(),
                            componentEndpointIds.getEndpointUuid(), componentEndpointIds.getCredentialUuid()));

            ServiceResponse<?> callbackResponse = processRequest(timeout, new ServiceRequestCallback()
            {
                @Override
                public String getRequestId()
                {
                    return correlationId;
                }

                @Override
                public void executeRequest(String requestId) throws Exception
                {
                    producer.publishDiscoverVcenter(requestMessage);
                }
            });

            DiscoveryResponseInfoMessage responseMessage = processResponse(callbackResponse, DiscoveryResponseInfoMessage.class);

            if (responseMessage != null && responseMessage.getMessageProperties() != null)
            {
                //TODO: Send some data and persist it
                repository.saveVCenterData();
            }
            else
            {
                LOGGER.error("Message is null");
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Exception occurred", e);
        }

        return taskResponse;
    }
}
