package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.UpdatePCIPassthruSVMResponseMessage;

/**
 * TODO: Document Usage
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class SetPciPassthroughResponseAdapter
        implements ServiceCallbackAdapter<UpdatePCIPassthruSVMResponseMessage, ServiceResponse<UpdatePCIPassthruSVMResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public SetPciPassthroughResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<UpdatePCIPassthruSVMResponseMessage> transform(final UpdatePCIPassthruSVMResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<UpdatePCIPassthruSVMResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final UpdatePCIPassthruSVMResponseMessage updatePCIPassthruSVMResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(updatePCIPassthruSVMResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<UpdatePCIPassthruSVMResponseMessage> getSourceClass()
    {
        return UpdatePCIPassthruSVMResponseMessage.class;
    }
}