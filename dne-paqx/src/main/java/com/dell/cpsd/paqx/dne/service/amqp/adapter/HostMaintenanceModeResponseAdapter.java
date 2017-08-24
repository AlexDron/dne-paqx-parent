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
import com.dell.cpsd.virtualization.capabilities.api.HostMaintenanceModeResponseMessage;

/**
 * Host maintenance mode response adapter.
 *
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved. Dell EMC Confidential/Proprietary Information
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class HostMaintenanceModeResponseAdapter
        implements ServiceCallbackAdapter<HostMaintenanceModeResponseMessage, ServiceResponse<HostMaintenanceModeResponseMessage>>
{
    private final ServiceCallbackRegistry serviceCallbackRegistry;

    public HostMaintenanceModeResponseAdapter(ServiceCallbackRegistry serviceCallbackRegistry)
    {
        this.serviceCallbackRegistry = serviceCallbackRegistry;
    }

    @Override
    public ServiceResponse<HostMaintenanceModeResponseMessage> transform(final HostMaintenanceModeResponseMessage responseMessage)
    {
        return new ServiceResponse<>(responseMessage.getMessageProperties().getCorrelationId(), responseMessage, null);
    }

    @Override
    public void consume(final IServiceCallback callback, final ServiceResponse<HostMaintenanceModeResponseMessage> responseMessage)
    {
        callback.handleServiceResponse(responseMessage);
    }

    @Override
    public IServiceCallback take(final HostMaintenanceModeResponseMessage hostMaintenanceModeResponseMessage)
    {
        return serviceCallbackRegistry.removeServiceCallback(hostMaintenanceModeResponseMessage.getMessageProperties().getCorrelationId());
    }

    @Override
    public Class<HostMaintenanceModeResponseMessage> getSourceClass()
    {
        return HostMaintenanceModeResponseMessage.class;
    }
}
