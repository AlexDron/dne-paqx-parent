package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.AddHostToDvSwitchResponseMessage;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class AddHostToDvSwitchResponseAdapter
        implements ServiceCallbackAdapter<AddHostToDvSwitchResponseMessage, ServiceResponse<AddHostToDvSwitchResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public AddHostToDvSwitchResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<AddHostToDvSwitchResponseMessage> transform(final AddHostToDvSwitchResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<AddHostToDvSwitchResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final AddHostToDvSwitchResponseMessage addHostToDvSwitchResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(addHostToDvSwitchResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<AddHostToDvSwitchResponseMessage> getSourceClass()
    {
        return AddHostToDvSwitchResponseMessage.class;
    }
}