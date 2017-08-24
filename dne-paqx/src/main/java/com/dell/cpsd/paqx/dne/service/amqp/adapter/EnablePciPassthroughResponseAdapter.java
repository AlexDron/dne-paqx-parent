/**
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.EnablePCIPassthroughResponseMessage;

/**
 * TODO: Document Usage
 *
 * <p/>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * <p/>
 *
 * @version 1.0
 * @since 1.0
 */
public class EnablePciPassthroughResponseAdapter
        implements ServiceCallbackAdapter<EnablePCIPassthroughResponseMessage, ServiceResponse<EnablePCIPassthroughResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public EnablePciPassthroughResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<EnablePCIPassthroughResponseMessage> transform(final EnablePCIPassthroughResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<EnablePCIPassthroughResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final EnablePCIPassthroughResponseMessage enablePCIPassthroughResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(enablePCIPassthroughResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<EnablePCIPassthroughResponseMessage> getSourceClass()
    {
        return EnablePCIPassthroughResponseMessage.class;
    }
}