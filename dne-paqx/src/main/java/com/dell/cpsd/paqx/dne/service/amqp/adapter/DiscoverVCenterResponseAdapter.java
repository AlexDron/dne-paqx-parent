package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.DiscoveryResponseInfoMessage;

/**
 * TODO: Document Usage
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class DiscoverVCenterResponseAdapter
        implements ServiceCallbackAdapter<DiscoveryResponseInfoMessage, ServiceResponse<DiscoveryResponseInfoMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public DiscoverVCenterResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<DiscoveryResponseInfoMessage> transform(final DiscoveryResponseInfoMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<DiscoveryResponseInfoMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final DiscoveryResponseInfoMessage listComponentResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(listComponentResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<DiscoveryResponseInfoMessage> getSourceClass()
    {
        return DiscoveryResponseInfoMessage.class;
    }
}
