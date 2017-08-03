package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.storage.capabilities.api.ListComponentResponseMessage;

/**
 * List ScaleIO Components Response Adapter
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class ListScaleIoComponentsResponseAdapter
        implements ServiceCallbackAdapter<ListComponentResponseMessage, ServiceResponse<ListComponentResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public ListScaleIoComponentsResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<ListComponentResponseMessage> transform(final ListComponentResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<ListComponentResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final ListComponentResponseMessage listComponentResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(listComponentResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<ListComponentResponseMessage> getSourceClass()
    {
        return ListComponentResponseMessage.class;
    }
}
