/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 */

package com.dell.cpsd.paqx.dne.service.amqp.adapter;

import com.dell.cpsd.service.common.client.callback.IServiceCallback;
import com.dell.cpsd.service.common.client.callback.ServiceResponse;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackAdapter;
import com.dell.cpsd.service.common.client.rpc.ServiceCallbackRegistry;
import com.dell.cpsd.virtualization.capabilities.api.HostPowerOperationResponseMessage;

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
public class RebootHostResponseAdapter
        implements ServiceCallbackAdapter<HostPowerOperationResponseMessage, ServiceResponse<HostPowerOperationResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public RebootHostResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<HostPowerOperationResponseMessage> transform(final HostPowerOperationResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<HostPowerOperationResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final HostPowerOperationResponseMessage hostPowerOperationResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(hostPowerOperationResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<HostPowerOperationResponseMessage> getSourceClass()
    {
        return HostPowerOperationResponseMessage.class;
    }
}